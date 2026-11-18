package br.com.CurriculoAi.controllers.formation;

import br.com.CurriculoAi.dto.formation.FormationRequest;
import br.com.CurriculoAi.dto.formation.FormationResponse;
import br.com.CurriculoAi.services.formation.FormationService;
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

@RestController
@Tag(name = "Candidato - Formações", description = "Formações acadêmicas do candidato autenticado")
@RequestMapping("/api/formations")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('CANDIDATO')")
public class FormationController {

    private final FormationService formationService;

    @Operation(
            summary = "Cadastrar formações",
            description = "Cadastra uma ou mais formações vinculadas a cursos do catálogo. Sem `finishDate`, a formação fica como em andamento."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Formações cadastradas com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PostMapping
    public ResponseEntity<List<FormationResponse>> registerFormationsByUser(
            @RequestBody List<@Valid FormationRequest> formationRequests) {

        List<FormationResponse> formationResponses = formationService.registerFormationsByUser(formationRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(formationResponses);
    }

    @Operation(
            summary = "Listar formações",
            description = "Retorna todas as formações do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de formações retornada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @GetMapping
    public ResponseEntity<List<FormationResponse>> findAllFormationsUser() {

        List<FormationResponse> formations = formationService.findAllFormationsUser();

        return ResponseEntity.ok(formations);
    }

    @Operation(
            summary = "Buscar formação por id",
            description = "Retorna uma formação do candidato autenticado. Retorna 404 se o id não existir ou pertencer a outro usuário."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Formação encontrada"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @GetMapping("/{formationId}")
    public ResponseEntity<FormationResponse> getFormationUserByFormationId(@Parameter(description = "Id da formação do candidato") @PathVariable Long formationId) {

        FormationResponse formationResponse = formationService.getFormationUserByFormationId(formationId);

        return ResponseEntity.ok(formationResponse);
    }

    @Operation(
            summary = "Atualizar uma formação",
            description = "Atualiza uma formação do candidato autenticado. Sem `finishDate`, a formação passa a ficar em andamento."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Formação atualizada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PutMapping("/{formationId}")
    public ResponseEntity<FormationResponse> updateFormation(
            @Parameter(description = "Id da formação do candidato")
            @PathVariable Long formationId,
            @RequestBody @Valid FormationRequest formationRequest) {

        FormationResponse formationResponse = formationService.updateFormation(formationId, formationRequest);

        return ResponseEntity.ok(formationResponse);
    }

    @Operation(
            summary = "Remover uma formação",
            description = "Remove uma formação do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Formação removida com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @DeleteMapping("/{formationId}")
    public ResponseEntity<Void> deleteFormation(@Parameter(description = "Id da formação do candidato") @PathVariable Long formationId) {

        formationService.deleteFormation(formationId);

        return ResponseEntity.noContent().build();
    }
}
