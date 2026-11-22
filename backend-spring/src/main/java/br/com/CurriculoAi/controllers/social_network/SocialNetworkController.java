package br.com.CurriculoAi.controllers.social_network;

import br.com.CurriculoAi.dto.social_network.SocialNetworkRequest;
import br.com.CurriculoAi.dto.social_network.SocialNetworkResponse;
import br.com.CurriculoAi.services.social_network.SocialNetworkService;
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
@Tag(name = "Catálogo - Redes Sociais", description = "Redes sociais (ex.: LinkedIn, GitHub) disponíveis para o candidato vincular ao perfil. Leitura liberada para usuários autenticados; cadastro permitido a candidatos e administradores; edição restrita ao perfil ADMIN.")
@RequestMapping("/api/social-networks")
@RequiredArgsConstructor
public class SocialNetworkController {

    private final SocialNetworkService socialNetworkService;

    @Operation(
            summary = "Cadastrar uma rede social",
            description = "Cadastra uma nova rede social no catálogo da aplicação. Requer autenticação como ADMIN ou CANDIDATO. Retorna 400 se já existir uma rede social com o mesmo nome."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rede social cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO')")
    @PostMapping
    public ResponseEntity<SocialNetworkResponse> createSocialNetwork(
            @Valid @RequestBody SocialNetworkRequest socialNetworkRequest) {

        SocialNetworkResponse socialNetworkResponse = socialNetworkService.createSocialNetwork(socialNetworkRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(socialNetworkResponse);
    }

    @Operation(
            summary = "Listar redes sociais",
            description = "Retorna todas as redes sociais cadastradas no catálogo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de redes sociais retornada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<SocialNetworkResponse>> findAllSocialNetworks() {

        List<SocialNetworkResponse> socialNetworks = socialNetworkService.findAllSocialNetworks();

        return ResponseEntity.ok(socialNetworks);
    }

    @Operation(
            summary = "Buscar rede social por id",
            description = "Retorna a rede social correspondente ao id informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rede social encontrada"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{socialNetworkId}")
    public ResponseEntity<SocialNetworkResponse> getSocialNetworkById(@Parameter(description = "Id da rede social") @PathVariable Long socialNetworkId) {

        SocialNetworkResponse socialNetworkResponse = socialNetworkService.getSocialNetworkById(socialNetworkId);

        return ResponseEntity.ok(socialNetworkResponse);
    }

    @Operation(
            summary = "Atualizar uma rede social",
            description = "Atualiza os dados de uma rede social do catálogo. Requer o perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rede social atualizada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{socialNetworkId}")
    public ResponseEntity<SocialNetworkResponse> updateSocialNetwork(@Parameter(description = "Id da rede social") @PathVariable Long socialNetworkId,
                                                                     @Valid @RequestBody SocialNetworkRequest socialNetworkRequest) {

        SocialNetworkResponse socialNetworkResponse = socialNetworkService.updateSocialNetwork(socialNetworkId, socialNetworkRequest);

        return ResponseEntity.ok(socialNetworkResponse);
    }
}