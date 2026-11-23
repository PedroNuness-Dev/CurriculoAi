package br.com.CurriculoAi.controllers.institution;

import br.com.CurriculoAi.dto.institution.InstitutionRequest;
import br.com.CurriculoAi.dto.institution.InstitutionResponse;
import br.com.CurriculoAi.services.institution.InstitutionService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static br.com.CurriculoAi.config.OpenApiResponseRefs.*;

@RestController
@Tag(name = "Catálogo - Instituições", description = "Instituições de ensino e certificadoras disponíveis para vincular às certificações. Leitura liberada para usuários autenticados; cadastro permitido a candidatos e administradores; edição restrita ao perfil ADMIN.")
@RequestMapping("/api/institutions")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;

    @Operation(
            summary = "Cadastrar uma instituição",
            description = "Cadastra uma nova instituição no catálogo da aplicação. Requer autenticação como ADMIN ou CANDIDATO. Retorna 400 se já existir uma instituição com o mesmo nome."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Instituição cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO')")
    @PostMapping
    public ResponseEntity<InstitutionResponse> createInstitution(
            @Valid @RequestBody InstitutionRequest institutionRequest) {

        InstitutionResponse institutionResponse = institutionService.createInstitution(institutionRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(institutionResponse);
    }

    @Operation(
            summary = "Listar instituições",
            description = "Retorna todas as instituições cadastradas no catálogo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de instituições retornada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<InstitutionResponse>> findAllInstitutions() {

        List<InstitutionResponse> institutions = institutionService.findAllInstitutions();

        return ResponseEntity.ok(institutions);
    }

    @Operation(
            summary = "Buscar instituição por id",
            description = "Retorna a instituição correspondente ao id informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Instituição encontrada"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{institutionId}")
    public ResponseEntity<InstitutionResponse> getInstitutionById(@Parameter(description = "Id da instituição") @PathVariable Long institutionId) {

        InstitutionResponse institutionResponse = institutionService.getInstitutionById(institutionId);

        return ResponseEntity.ok(institutionResponse);
    }

    @Operation(
            summary = "Atualizar uma instituição",
            description = "Atualiza os dados de uma instituição do catálogo. Requer o perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Instituição atualizada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{institutionId}")
    public ResponseEntity<InstitutionResponse> updateInstitution(@Parameter(description = "Id da instituição") @PathVariable Long institutionId,
                                                                 @Valid @RequestBody InstitutionRequest institutionRequest) {

        InstitutionResponse institutionResponse = institutionService.updateInstitution(institutionId, institutionRequest);

        return ResponseEntity.ok(institutionResponse);
    }
}