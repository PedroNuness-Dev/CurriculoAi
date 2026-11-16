package br.com.CurriculoAi.controllers.projects;

import br.com.CurriculoAi.dto.project.ProjectRequest;
import br.com.CurriculoAi.dto.project.ProjectResponse;
import br.com.CurriculoAi.services.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projeto Controller", description = "Responsável pelas ações relacionadas aos projetos do usuário")
public class ProjetoController {

    private final ProjectService projectService;

    @Operation(summary = "Cadastrar projetos do usuário autenticado")
    @PostMapping
    public ResponseEntity<List<ProjectResponse>> registerProjects(@RequestBody List<@Valid ProjectRequest> projectRequests){

        List<ProjectResponse> projectResponses = projectService.registerProjects(projectRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponses);
    }

    @Operation(summary = "Listar os projetos do usuário autenticado")
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> findAllProjectsOfUser(){

        List<ProjectResponse> projectResponses = projectService.findAllProjectsOfUser();

        return ResponseEntity.ok(projectResponses);
    }

    @Operation(summary = "Buscar um projeto do usuário autenticado pelo id")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long projectId){

        ProjectResponse projectResponse = projectService.getProjectById(projectId);

        return ResponseEntity.ok(projectResponse);
    }

    @Operation(summary = "Atualizar um projeto do usuário autenticado (substitui a lista de habilidades)")
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long projectId,
            @RequestBody @Valid ProjectRequest projectRequest){

        ProjectResponse projectResponse = projectService.updateProject(projectId, projectRequest);

        return ResponseEntity.ok(projectResponse);
    }

    @Operation(summary = "Remover um projeto do usuário autenticado")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId){

        projectService.deleteProject(projectId);

        return ResponseEntity.noContent().build();
    }
}
