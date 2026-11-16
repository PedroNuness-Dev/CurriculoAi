package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserAbilityResponse;
import br.com.CurriculoAi.services.user.UserAbilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
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
@Tag(name = "Candidato - Habilidades", description = "Habilidades do candidato autenticado, vinculadas a partir do catálogo")
@RequestMapping("/api/users/abilities")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CANDIDATO')")
public class UserAbilityController {

    private final UserAbilityService userAbilityService;

    @Operation(
            summary = "Adicionar habilidades",
            description = "Adiciona as habilidades informadas (lista de ids do catálogo) às que o candidato já possui, sem duplicar. Retorna 404 se algum id não existir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Habilidades adicionadas com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PostMapping
    public ResponseEntity<UserAbilityResponse> registerUserAbilities(@RequestBody List<@NotNull Long> abilitiesIds){

        UserAbilityResponse userAbilityResponse = userAbilityService.registerUserAbilities(abilitiesIds);

        return ResponseEntity.status(HttpStatus.CREATED).body(userAbilityResponse);
    }

    @Operation(
            summary = "Listar habilidades",
            description = "Retorna as habilidades do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Habilidades retornadas com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @GetMapping
    public ResponseEntity<UserAbilityResponse> getAbilitiesOfUser(){

        UserAbilityResponse userAbilityResponse = userAbilityService.getAbilitiesOfUser();

        return ResponseEntity.ok(userAbilityResponse);
    }

    @Operation(
            summary = "Substituir habilidades",
            description = "Substitui a lista inteira de habilidades do candidato pelos ids informados. A lista não pode ser vazia: para remover todas, use o DELETE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Habilidades substituídas com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PutMapping
    public ResponseEntity<UserAbilityResponse> replaceUserAbilities(@RequestBody List<@NotNull Long> abilitiesIds){

        UserAbilityResponse userAbilityResponse = userAbilityService.replaceUserAbilities(abilitiesIds);

        return ResponseEntity.ok(userAbilityResponse);
    }

    @Operation(
            summary = "Remover todas as habilidades",
            description = "Remove todos os vínculos de habilidades do candidato. As habilidades continuam no catálogo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Habilidades removidas com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @DeleteMapping
    public ResponseEntity<Void> removeAllUserAbilities(){

        userAbilityService.removeAllUserAbilities();

        return ResponseEntity.noContent().build();
    }
}
