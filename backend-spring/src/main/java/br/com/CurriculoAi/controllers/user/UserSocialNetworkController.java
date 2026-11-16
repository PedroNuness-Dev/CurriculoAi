package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserSocialNetworkRequest;
import br.com.CurriculoAi.dto.user.UserSocialNetworkResponse;
import br.com.CurriculoAi.services.user.UserSocialNetworkService;
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
@Tag(name = "Candidato - Redes Sociais", description = "Links das redes sociais do candidato autenticado")
@RequestMapping("/api/users/social-networks")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CANDIDATO')")
public class UserSocialNetworkController {

    private final UserSocialNetworkService userSocialNetworkService;

    @Operation(
            summary = "Cadastrar redes sociais",
            description = "Cadastra um ou mais links de redes sociais do catálogo. Retorna 404 se alguma rede social não existir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Redes sociais cadastradas com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PostMapping
    public ResponseEntity<List<UserSocialNetworkResponse>> registerUserSocialNetworks(
            @RequestBody List<@Valid UserSocialNetworkRequest> userSocialNetworkRequests){

        List<UserSocialNetworkResponse> userSocialNetworkResponses = userSocialNetworkService.registerUserSocialNetworks(userSocialNetworkRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(userSocialNetworkResponses);
    }

    @Operation(
            summary = "Listar redes sociais",
            description = "Retorna todas as redes sociais do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de redes sociais retornada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @GetMapping
    public ResponseEntity<List<UserSocialNetworkResponse>> getUserSocialNetworksOfUser(){

        List<UserSocialNetworkResponse> userSocialNetworkResponses = userSocialNetworkService.getUserSocialNetworksOfUser();

        return ResponseEntity.ok(userSocialNetworkResponses);
    }

    @Operation(
            summary = "Buscar rede social por id",
            description = "Retorna uma rede social do candidato autenticado. Retorna 404 se o id não existir ou pertencer a outro usuário."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rede social encontrada"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @GetMapping("/{userSocialNetworkId}")
    public ResponseEntity<UserSocialNetworkResponse> getUserSocialNetworkById(@Parameter(description = "Id da rede social do candidato") @PathVariable Long userSocialNetworkId){

        UserSocialNetworkResponse userSocialNetworkResponse = userSocialNetworkService.getUserSocialNetworkById(userSocialNetworkId);

        return ResponseEntity.ok(userSocialNetworkResponse);
    }

    @Operation(
            summary = "Atualizar uma rede social",
            description = "Atualiza uma rede social do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rede social atualizada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PutMapping("/{userSocialNetworkId}")
    public ResponseEntity<UserSocialNetworkResponse> updateUserSocialNetwork(
            @Parameter(description = "Id da rede social do candidato")
            @PathVariable Long userSocialNetworkId,
            @RequestBody @Valid UserSocialNetworkRequest userSocialNetworkRequest){

        UserSocialNetworkResponse userSocialNetworkResponse =
                userSocialNetworkService.updateUserSocialNetwork(userSocialNetworkId, userSocialNetworkRequest);

        return ResponseEntity.ok(userSocialNetworkResponse);
    }

    @Operation(
            summary = "Remover uma rede social",
            description = "Remove uma rede social do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Rede social removida com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @DeleteMapping("/{userSocialNetworkId}")
    public ResponseEntity<Void> deleteUserSocialNetwork(@Parameter(description = "Id da rede social do candidato") @PathVariable Long userSocialNetworkId){

        userSocialNetworkService.deleteUserSocialNetwork(userSocialNetworkId);

        return ResponseEntity.noContent().build();
    }
}
