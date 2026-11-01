package br.com.CurriculoAi.controllers;

import br.com.CurriculoAi.dto.request.ProjetoDtoRequest;
import br.com.CurriculoAi.dto.response.ProjetoDtoResponse;
import br.com.CurriculoAi.services.project.ProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projeto")
@RequiredArgsConstructor
@Tag(name = "Projeto Controller", description = "Responsável pelas ações relacionadas aos projetos do usuário")
public class ProjetoController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<List<ProjetoDtoResponse>> registerProjects(@RequestBody List<ProjetoDtoRequest> projetosParaCadastro){

        List<ProjetoDtoResponse> projetosSalvos = projectService.registerProjects(projetosParaCadastro);

        return ResponseEntity.ok(projetosSalvos);
    }
}
