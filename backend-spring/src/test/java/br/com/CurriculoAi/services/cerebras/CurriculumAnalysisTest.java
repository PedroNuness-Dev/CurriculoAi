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
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CurriculumAnalysisTest {
    private final CerebrasService cerebras = new CerebrasService(null, null);
    private final CerebrasController controller = new CerebrasController(cerebras, null, new CurriculumTextExtractor());

    private MockRestServiceServer provider(String feedback) throws Exception {
        return provider(feedback, null);
    }

    private MockRestServiceServer provider(String feedback, String vaga) throws Exception {
        ReflectionTestUtils.setField(cerebras, "apiKey", "test");
        ReflectionTestUtils.setField(cerebras, "apiUrl", "https://example.test/chat");
        var client = (RestTemplate) ReflectionTestUtils.getField(cerebras, "restTemplate");
        var server = MockRestServiceServer.bindTo(client).build();
        var response = Map.of("choices", List.of(Map.of("message", Map.of("content", feedback))));
        String input = vaga == null ? "Experiencia com Java" : new ObjectMapper()
                .writeValueAsString(Map.of("curriculo", "Experiencia com Java", "vaga", vaga));
        server.expect(requestTo("https://example.test/chat"))
                .andExpect(header("Authorization", "Bearer test"))
                .andExpect(jsonPath("$.messages[1].content").value(input))
                .andRespond(withSuccess(new ObjectMapper().writeValueAsString(response), MediaType.APPLICATION_JSON));
        return server;
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
                {"resumo":"Experiência em Java","pontosFortes":["Tecnologia explícita"],
                 "pontosDeMelhoria":[],"sugestoes":["Descreva um projeto."]}
                """);
        assertEquals(List.of("Descreva um projeto."), controller.analyze(pdf(true), "  ").sugestoes());
        server.verify();
    }

    @Test
    void comparesJobRequirementsWhenDescriptionIsProvided() throws Exception {
        String vaga = "Java obrigatório. Kubernetes desejável.";
        var server = provider("""
                {"resumo":"Experiência em Java","pontosFortes":[],"pontosDeMelhoria":[],"sugestoes":[],
                 "compatibilidade":{"resumo":"Há evidência do requisito obrigatório.",
                 "requisitosAtendidos":["Java"],"requisitosSemEvidencia":["Kubernetes"]}}
                """, vaga);
        var result = controller.analyze(pdf(true), vaga).compatibilidade();
        assertEquals(List.of("Java"), result.requisitosAtendidos());
        assertEquals(List.of("Kubernetes"), result.requisitosSemEvidencia());
        server.verify();
    }

    @Test
    void rejectsUnreadableOrBlankDocument() throws Exception {
        var blank = pdf(false);
        assertThrows(IllegalArgumentException.class, () -> controller.analyze(blank, null));
        assertThrows(IllegalArgumentException.class,
                () -> controller.analyze(new MockMultipartFile("file", new byte[]{1, 2}), null));
    }

    @Test
    void rejectsIncompleteFeedback() throws Exception {
        var server = provider("{\"resumo\":\"Teste\"}");
        var file = pdf(true);
        var error = assertThrows(ResponseStatusException.class, () -> controller.analyze(file, null));
        assertEquals(HttpStatus.BAD_GATEWAY, error.getStatusCode());
        server.verify();
    }
}
