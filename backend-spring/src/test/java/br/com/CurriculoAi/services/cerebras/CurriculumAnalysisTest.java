package br.com.CurriculoAi.services.cerebras;

import br.com.CurriculoAi.controllers.cerebras.CerebrasController;
import br.com.CurriculoAi.services.pdf.CurriculumTextExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CurriculumAnalysisTest {
    private final br.com.CurriculoAi.client.cerebras.CerebrasClient client = org.mockito.Mockito.mock(br.com.CurriculoAi.client.cerebras.CerebrasClient.class);
    private final CerebrasService cerebras = new CerebrasService(client, null, null);
    private final CerebrasController controller = new CerebrasController(cerebras, null, new CurriculumTextExtractor());

    private Runnable provider(String feedback) throws Exception {
        return provider(feedback, null);
    }

    private Runnable provider(String feedback, String jobDescription) throws Exception {
        ReflectionTestUtils.setField(cerebras, "model", "test-model");
        var response = Map.of("choices", List.of(Map.of("message", Map.of("content", feedback))));
        String input = jobDescription == null ? "Experiencia com Java" : new ObjectMapper()
                .writeValueAsString(Map.of("resume", "Experiencia com Java", "jobDescription", jobDescription));
        org.mockito.Mockito.when(client.complete(org.mockito.ArgumentMatchers.anyString()))
                .thenAnswer(invocation -> {
                    var payload = new ObjectMapper().readTree((String) invocation.getArgument(0));
                    assertEquals("test-model", payload.path("model").asText());
                    assertEquals(input, payload.at("/messages/1/content").asText());
                    return new ObjectMapper().writeValueAsString(response);
                });
        return () -> org.mockito.Mockito.verify(client).complete(org.mockito.ArgumentMatchers.anyString());
    }

    private MockMultipartFile pdf(boolean withText) throws Exception {
        try (var document = new PDDocument(); var output = new ByteArrayOutputStream()) {
            var page = new PDPage();
            document.addPage(page);
            if (withText) {
                try (var stream = new PDPageContentStream(document, page)) {
                    stream.beginText();
                    stream.setFont(PDType1Font.HELVETICA, 12);
                    stream.showText("Experiencia com Java");
                    stream.endText();
                }
            }
            document.save(output);
            return new MockMultipartFile("file", output.toByteArray());
        }
    }

    @Test
    void returnsFeedbackForUploadedPdf() throws Exception {
        var server = provider("""
                {"summary":"Experiência em Java","strengths":["Tecnologia explícita"],
                 "weaknesses":[],"suggestions":["Descreva um projeto."],"structureScore":{"value":60,"rationale":"Seções pouco claras"}, "contentScore":{"value":50,"rationale":"Faltam contribuições"}, "improvements":[]}
                """);
        assertEquals(List.of("Descreva um projeto."), controller.analyze(pdf(true), "  ").getBody().suggestions());
        server.run();
    }

    @Test
    void comparesJobRequirementsWhenDescriptionIsProvided() throws Exception {
        String jobDescription = "Java obrigatório. Kubernetes desejável.";
        var server = provider("""
                {"summary":"Experiência em Java","strengths":[],"weaknesses":[],"suggestions":[],
                 "structureScore":{"value":60,"rationale":"Seções pouco claras"}, "contentScore":{"value":50,"rationale":"Faltam contribuições"}, "improvements":[],"compatibility":{"score":{"value":67,"rationale":"Java obrigatório comprovado, Kubernetes desejável sem evidência"},"summary":"Há evidência do requisito obrigatório.",
                 "matchedRequirements":["Java"],"requirementsWithoutEvidence":["Kubernetes"]}}
                """, jobDescription);
        var result = controller.analyze(pdf(true), jobDescription).getBody().compatibility();
        assertEquals(List.of("Java"), result.matchedRequirements());
        assertEquals(List.of("Kubernetes"), result.requirementsWithoutEvidence());
        server.run();
    }

    @Test
    void removesExampleWithUnsupportedMetric() {
        var item = new br.com.CurriculoAi.dto.cerebras.Improvement(
                br.com.CurriculoAi.dto.cerebras.Priority.HIGH,
                "Experiência", "Testei 12 rotas.", "Impacto desconhecido", "Descreva o impacto",
                "Testei 12 rotas e reduzi falhas em 30%.", "Qual foi o resultado?");
        assertNull(item.validateExample().example());
        assertEquals(item.question(), item.validateExample().question());
    }

    @Test
    void rejectsInventedQuotation() throws Exception {
        provider("""
                {"summary":"Resumo","strengths":[],"weaknesses":[],"suggestions":[],
                 "structureScore":{"value":60,"rationale":"Seções pouco claras"}, "contentScore":{"value":50,"rationale":"Faltam contribuições"}, "improvements":[{"priority":"HIGH","section":"Experiência","originalExcerpt":"Fiz deploy",
                 "problem":"Falta contexto","suggestion":"Descreva o projeto"}]}
                """);
        var file = pdf(true);
        assertEquals(HttpStatus.BAD_GATEWAY,
                assertThrows(ResponseStatusException.class, () -> controller.analyze(file, null)).getStatusCode());
    }

    @Test
    void rejectsUnreadableOrBlankDocument() throws Exception {
        var blank = pdf(false);
        assertThrows(IllegalArgumentException.class, () -> controller.analyze(blank, null));
        assertThrows(IllegalArgumentException.class,
                () -> controller.analyze(new MockMultipartFile("file", new byte[]{1, 2}), null));
    }

    @Test
    void validatesScoresWithoutTurningMissingEvidenceIntoZero() {
        assertNull(new br.com.CurriculoAi.dto.cerebras.AssessmentScore(null, "Vaga sem requisitos").value());
        assertThrows(IllegalArgumentException.class, () -> new br.com.CurriculoAi.dto.cerebras.AssessmentScore(101, "Inválido"));
        assertThrows(IllegalArgumentException.class, () -> new br.com.CurriculoAi.dto.cerebras.AssessmentScore(-1, "Inválido"));
    }

    @Test
    void rejectsIncompleteFeedback() throws Exception {
        var server = provider("{\"summary\":\"Teste\"}");
        var file = pdf(true);
        var error = assertThrows(ResponseStatusException.class, () -> controller.analyze(file, null));
        assertEquals(HttpStatus.BAD_GATEWAY, error.getStatusCode());
        server.run();
    }
}
