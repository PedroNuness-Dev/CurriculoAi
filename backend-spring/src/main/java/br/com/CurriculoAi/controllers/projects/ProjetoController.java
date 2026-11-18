package br.com.CurriculoAi.controllers.projects;

import br.com.CurriculoAi.dto.project.ProjectRequest;
import br.com.CurriculoAi.dto.project.ProjectResponse;
import br.com.CurriculoAi.services.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static br.com.CurriculoAi.config.OpenApiResponseRefs.*;

@Validated
@RestController
@Tag(name = "Candidato - Projetos", description = "Projetos pessoais ou profissionais do candidato autenticado")
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CANDIDATO')")
public class ProjetoController {

    private final ProjectService projectService;

    @Operation(
            summary = "Cadastrar projetos",
            description = "Cadastra um ou mais projetos com as habilidades utilizadas (ids do catálogo). Retorna 404 se alguma habilidade não existir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Projetos cadastrados com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PostMapping
    public ResponseEntity<List<ProjectResponse>> registerProjects(@RequestBody List<@Valid ProjectRequest> projectRequests){

        List<ProjectResponse> projectResponses = projectService.registerProjects(projectRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponses);
    }

    @Operation(
            summary = "Listar projetos",
            description = "Retorna todos os projetos do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de projetos retornada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> findAllProjectsOfUser(){

        List<ProjectResponse> projectResponses = projectService.findAllProjectsOfUser();

        return ResponseEntity.ok(projectResponses);
    }

    @Operation(
            summary = "Buscar projeto por id",
            description = "Retorna um projeto do candidato autenticado. Retorna 404 se o id não existir ou pertencer a outro usuário."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projeto encontrado"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(@Parameter(description = "Id do projeto do candidato") @PathVariable Long projectId){

        ProjectResponse projectResponse = projectService.getProjectById(projectId);

        return ResponseEntity.ok(projectResponse);
    }

    @Operation(
            summary = "Atualizar um projeto",
            description = "Atualiza um projeto do candidato autenticado. A lista de habilidades informada substitui a atual."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projeto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @Parameter(description = "Id do projeto do candidato")
            @PathVariable Long projectId,
            @RequestBody @Valid ProjectRequest projectRequest){

        ProjectResponse projectResponse = projectService.updateProject(projectId, projectRequest);

        return ResponseEntity.ok(projectResponse);
    }

    @Operation(
            summary = "Remover um projeto",
            description = "Remove um projeto do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Projeto removido com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@Parameter(description = "Id do projeto do candidato") @PathVariable Long projectId){

        projectService.deleteProject(projectId);

        return ResponseEntity.noContent().build();
    }
}
