package br.com.CurriculoAi.controllers.cerebras;

import br.com.CurriculoAi.services.cerebras.CerebrasService;
import br.com.CurriculoAi.services.pdf.PdfService;
import br.com.CurriculoAi.services.pdf.CurriculumTextExtractor;
import br.com.CurriculoAi.dto.cerebras.CurriculumAnalysis;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static br.com.CurriculoAi.config.OpenApiResponseRefs.*;

@RestController
@Tag(name = "Currículo", description = "Geração do currículo em PDF com IA a partir do perfil do candidato")
@RequestMapping("/api/cerebras")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CANDIDATO')")
public class CerebrasController {

    private final CerebrasService cerebrasService;
    private final  PdfService pdfService;
    private final CurriculumTextExtractor textExtractor;

    @Operation(summary = "Analisar o texto de um currículo em PDF",
            description = "Requer JWT. Envie o PDF em file e, opcionalmente, a descrição da vaga em vaga para comparar os requisitos.")
    @PostMapping(value = "/curriculum/analysis", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CurriculumAnalysis analyze(@RequestPart(value = "file", required = false) MultipartFile file,
                                      @RequestPart(value = "vaga", required = false) String vaga) {
        return cerebrasService.analyze(textExtractor.extract(file), vaga);
    }

    @Operation(summary = "Gerar PDF revisado com as respostas do candidato")
    @PostMapping(value = "/curriculum/revision", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> revise(@RequestPart("file") MultipartFile file,
            @RequestPart(value = "vaga", required = false) String vaga,
            @RequestPart("respostas") String answers) throws Exception {
        String revised = cerebrasService.revise(textExtractor.extract(file), vaga, answers);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=curriculo-revisado.pdf")
                .contentType(MediaType.APPLICATION_PDF).body(pdfService.generatePdfFromText(revised));
    }

    @Operation(
            summary = "Gerar currículo em PDF",
            description = "Monta o currículo do candidato autenticado com IA a partir do perfil cadastrado e retorna o arquivo PDF (`Content-Disposition: attachment; filename=curriculo.pdf`)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF gerado com sucesso",
                    content = @Content(mediaType = "application/pdf", schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "502", ref = BAD_GATEWAY),
            @ApiResponse(responseCode = "503", ref = SERVICE_UNAVAILABLE)
    })
    @PostMapping("/curriculum")
    public ResponseEntity<byte[]> generateCurriculum() throws Exception {

        String markdown = cerebrasService.gerarMarkdawn();

        byte[] curriculum = pdfService.generatePdfFromMarkdown(markdown);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=curriculo.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(curriculum);
    }
}
