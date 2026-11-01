package br.com.CurriculoAi.controllers.social_network;

import br.com.CurriculoAi.dto.social_network.SocialNetworkRequest;
import br.com.CurriculoAi.dto.social_network.SocialNetworkResponse;
import br.com.CurriculoAi.services.social_network.SocialNetworkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social-networks")
@RequiredArgsConstructor
@Tag(name = "Social Network Controller", description = "Responsável por controlar as ações relacionadas às redes sociais do sistema")
public class SocialNetworkController {

    private final SocialNetworkService socialNetworkService;

    @Operation(summary = "Cadastrar uma rede social")
    @PostMapping
    public ResponseEntity<SocialNetworkResponse> createSocialNetwork(
            @Valid @RequestBody SocialNetworkRequest socialNetworkRequest) {

        SocialNetworkResponse socialNetworkResponse = socialNetworkService.createSocialNetwork(socialNetworkRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(socialNetworkResponse);
    }

    @Operation(summary = "Listar todas as redes sociais")
    @GetMapping
    public ResponseEntity<List<SocialNetworkResponse>> findAllSocialNetworks() {

        List<SocialNetworkResponse> socialNetworks = socialNetworkService.findAllSocialNetworks();

        return ResponseEntity.ok(socialNetworks);
    }

    @Operation(summary = "Buscar uma rede social pelo id")
    @GetMapping("/{socialNetworkId}")
    public ResponseEntity<SocialNetworkResponse> getSocialNetworkById(@PathVariable Long socialNetworkId) {

        SocialNetworkResponse socialNetworkResponse = socialNetworkService.getSocialNetworkById(socialNetworkId);

        return ResponseEntity.ok(socialNetworkResponse);
    }

    @Operation(summary = "Atualizar uma rede social")
    @PutMapping("/{socialNetworkId}")
    public ResponseEntity<SocialNetworkResponse> updateSocialNetwork(@PathVariable Long socialNetworkId,
                                                                     @Valid @RequestBody SocialNetworkRequest socialNetworkRequest) {

        SocialNetworkResponse socialNetworkResponse = socialNetworkService.updateSocialNetwork(socialNetworkId, socialNetworkRequest);

        return ResponseEntity.ok(socialNetworkResponse);
    }
}