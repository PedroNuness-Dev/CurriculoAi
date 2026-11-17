package br.com.CurriculoAi.controllers.cerebras;

import br.com.CurriculoAi.services.cerebras.CerebrasService;
import br.com.CurriculoAi.services.pdf.PdfService;
import br.com.CurriculoAi.services.pdf.CurriculumTextExtractor;
import br.com.CurriculoAi.dto.cerebras.CurriculumAnalysis;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cerebras")
@RequiredArgsConstructor
@Tag(name = "Cerebras Controller", description = "Responsável por controlar as ações relacionadas ao curriculo do usuário")
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


    @Operation(summary = "Gerar o `Curriculo` do usuário",
            description = "OBS: o usuário precisa estar `AUTENTICADO` com JWT")
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
