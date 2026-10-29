package br.com.CurriculoAi.controllers;

import br.com.CurriculoAi.dto.response.CurriculosDtoResponse;
import br.com.CurriculoAi.dto.response.UsuarioFullContentDtoResponse;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.services.cerebras.CerebrasService;
import br.com.CurriculoAi.services.curriculum.CurriculoUsuarioService;
import br.com.CurriculoAi.services.pdf.PdfService;
import br.com.CurriculoAi.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gerar")
@RequiredArgsConstructor
@Tag(name = "Cerebras Controller", description = "Responsável por controlar as ações relacionadas ao curriculo do usuário")
public class CerebrasController {

    private final CerebrasService cerebrasService;
    private final  PdfService pdfService;
    private final UserService userService;
    private final CurriculoUsuarioService curriculoUsuarioService;

    @Operation(summary = "Gerar o `Curriculo` do usuário",
            description = "OBS: o usuário precisa estar `AUTENTICADO` com JWT")
    @PostMapping("/pdf")
    public ResponseEntity<byte[]> gerarPdf() throws Exception {

        UsuarioFullContentDtoResponse curriculo = userService.me();

        String markdown = cerebrasService.gerarMarkdawn(curriculo);

        byte[] pdf = pdfService.generatePdfFromMarkdown(markdown);

        //salva o curriculo no usuario autenticado
        User userAutenticado = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        curriculoUsuarioService.salvar(pdf, userAutenticado);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=curriculo.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping
    public ResponseEntity<List<CurriculosDtoResponse>> buscarCurriculos(){

        List<CurriculosDtoResponse> curriculosDtoResponses = cerebrasService.buscarCurriculos();

        return ResponseEntity.ok(curriculosDtoResponses);
    }
}