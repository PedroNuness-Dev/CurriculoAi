package br.com.CurriculoAi.controllers.ability;

import br.com.CurriculoAi.dto.ability.AbilityRequest;
import br.com.CurriculoAi.dto.ability.AbilityResponse;
import br.com.CurriculoAi.services.ability.AbilityService;
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
@Tag(name = "Catálogo - Habilidades", description = "Habilidades técnicas e comportamentais disponíveis para candidatos e projetos. Leitura liberada para usuários autenticados; cadastro permitido a candidatos e administradores; edição restrita ao perfil ADMIN.")
@RequestMapping("/api/abilities")
@RequiredArgsConstructor
public class AbilityController {

    private final AbilityService abilityService;

    @Operation(
            summary = "Cadastrar uma habilidade",
            description = "Cadastra uma nova habilidade no catálogo da aplicação. Requer autenticação como ADMIN ou CANDIDATO. Retorna 400 se já existir uma habilidade com o mesmo nome."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Habilidade cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO')")
    @PostMapping
    public ResponseEntity<AbilityResponse> createAbility(@Valid @RequestBody AbilityRequest abilityRequest) {

        AbilityResponse abilityResponse = abilityService.createAbility(abilityRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(abilityResponse);
    }

    @Operation(
            summary = "Listar habilidades",
            description = "Retorna todas as habilidades cadastradas no catálogo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de habilidades retornada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<AbilityResponse>> findAllAbilities() {

        List<AbilityResponse> abilities = abilityService.findAllAbilities();

        return ResponseEntity.ok(abilities);
    }

    @Operation(
            summary = "Buscar habilidade por id",
            description = "Retorna a habilidade correspondente ao id informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Habilidade encontrada"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{abilityId}")
    public ResponseEntity<AbilityResponse> getAbilityById(@Parameter(description = "Id da habilidade") @PathVariable Long abilityId) {

        AbilityResponse abilityResponse = abilityService.getAbilityById(abilityId);

        return ResponseEntity.ok(abilityResponse);
    }

    @Operation(
            summary = "Atualizar uma habilidade",
            description = "Atualiza os dados de uma habilidade do catálogo. Requer o perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Habilidade atualizada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{abilityId}")
    public ResponseEntity<AbilityResponse> updateAbility(@Parameter(description = "Id da habilidade") @PathVariable Long abilityId,
                                                         @Valid @RequestBody AbilityRequest abilityRequest) {

        AbilityResponse abilityResponse = abilityService.updateAbility(abilityId, abilityRequest);

        return ResponseEntity.ok(abilityResponse);
    }
}