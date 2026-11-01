package br.com.CurriculoAi.controllers;

import br.com.CurriculoAi.dto.project.ProjectRequest;
import br.com.CurriculoAi.dto.project.ProjectResponse;
import br.com.CurriculoAi.services.project.ProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projeto Controller", description = "Responsável pelas ações relacionadas aos projetos do usuário")
public class ProjetoController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<List<ProjectResponse>> registerProjects(@RequestBody List<@Valid ProjectRequest> projectRequests){

        List<ProjectResponse> projectResponses = projectService.registerProjects(projectRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponses);
    }
}
