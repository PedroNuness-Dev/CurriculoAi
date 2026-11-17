package br.com.CurriculoAi.controllers.experience;

import br.com.CurriculoAi.dto.experience.ExperienceRequest;
import br.com.CurriculoAi.dto.experience.ExperienceResponse;
import br.com.CurriculoAi.services.experience.ExperienciaService;
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
@Tag(name = "Candidato - Experiências", description = "Experiências profissionais do candidato autenticado")
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CANDIDATO')")
public class ExperienceController {

    private final ExperienciaService experienciaService;

    @Operation(
            summary = "Cadastrar experiências",
            description = "Cadastra uma ou mais experiências vinculadas a áreas e empresas do catálogo. Sem `finishDate`, a experiência é marcada como trabalho atual."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Experiências cadastradas com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PostMapping
    public ResponseEntity<List<ExperienceResponse>> registerExperiences(@RequestBody List<@Valid ExperienceRequest> experienceRequests){

        List<ExperienceResponse> experienceResponses = experienciaService.registerExperiences(experienceRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(experienceResponses);
    }

    @Operation(
            summary = "Listar experiências",
            description = "Retorna todas as experiências do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de experiências retornada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @GetMapping
    public ResponseEntity<List<ExperienceResponse>> findAllExperiencesOfUser(){

        List<ExperienceResponse> experienceResponses = experienciaService.findAllExperiencesOfUser();

        return ResponseEntity.ok(experienceResponses);
    }

    @Operation(
            summary = "Buscar experiência por id",
            description = "Retorna uma experiência do candidato autenticado. Retorna 404 se o id não existir ou pertencer a outro usuário."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Experiência encontrada"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @GetMapping("/{experienceId}")
    public ResponseEntity<ExperienceResponse> getExperienceById(@Parameter(description = "Id da experiência do candidato") @PathVariable Long experienceId){

        ExperienceResponse experienceResponse = experienciaService.getExperienceById(experienceId);

        return ResponseEntity.ok(experienceResponse);
    }

    @Operation(
            summary = "Atualizar uma experiência",
            description = "Atualiza uma experiência do candidato autenticado. Sem `finishDate`, a experiência passa a ser o trabalho atual."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Experiência atualizada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PutMapping("/{experienceId}")
    public ResponseEntity<ExperienceResponse> updateExperience(
            @Parameter(description = "Id da experiência do candidato")
            @PathVariable Long experienceId,
            @RequestBody @Valid ExperienceRequest experienceRequest){

        ExperienceResponse experienceResponse = experienciaService.updateExperience(experienceId, experienceRequest);

        return ResponseEntity.ok(experienceResponse);
    }

    @Operation(
            summary = "Remover uma experiência",
            description = "Remove uma experiência do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Experiência removida com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @DeleteMapping("/{experienceId}")
    public ResponseEntity<Void> deleteExperience(@Parameter(description = "Id da experiência do candidato") @PathVariable Long experienceId){

        experienciaService.deleteExperience(experienceId);

        return ResponseEntity.noContent().build();
    }
}
