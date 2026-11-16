package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserSocialNetworkRequest;
import br.com.CurriculoAi.dto.user.UserSocialNetworkResponse;
import br.com.CurriculoAi.services.user.UserSocialNetworkService;
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
@RequestMapping("/api/users/social-networks")
@Tag(name = "User Social Network Controller")
@RequiredArgsConstructor
public class UserSocialNetworkController {

    private final UserSocialNetworkService userSocialNetworkService;

    @Operation(summary = "Cadastrar redes sociais do usuário autenticado")
    @PostMapping
    public ResponseEntity<List<UserSocialNetworkResponse>> registerUserSocialNetworks(
            @RequestBody List<@Valid UserSocialNetworkRequest> userSocialNetworkRequests){

        List<UserSocialNetworkResponse> userSocialNetworkResponses = userSocialNetworkService.registerUserSocialNetworks(userSocialNetworkRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(userSocialNetworkResponses);
    }

    @Operation(summary = "Listar as redes sociais do usuário autenticado")
    @GetMapping
    public ResponseEntity<List<UserSocialNetworkResponse>> getUserSocialNetworksOfUser(){

        List<UserSocialNetworkResponse> userSocialNetworkResponses = userSocialNetworkService.getUserSocialNetworksOfUser();

        return ResponseEntity.ok(userSocialNetworkResponses);
    }

    @Operation(summary = "Buscar uma rede social do usuário autenticado pelo id")
    @GetMapping("/{userSocialNetworkId}")
    public ResponseEntity<UserSocialNetworkResponse> getUserSocialNetworkById(@PathVariable Long userSocialNetworkId){

        UserSocialNetworkResponse userSocialNetworkResponse = userSocialNetworkService.getUserSocialNetworkById(userSocialNetworkId);

        return ResponseEntity.ok(userSocialNetworkResponse);
    }

    @Operation(summary = "Atualizar uma rede social do usuário autenticado")
    @PutMapping("/{userSocialNetworkId}")
    public ResponseEntity<UserSocialNetworkResponse> updateUserSocialNetwork(
            @PathVariable Long userSocialNetworkId,
            @RequestBody @Valid UserSocialNetworkRequest userSocialNetworkRequest){

        UserSocialNetworkResponse userSocialNetworkResponse =
                userSocialNetworkService.updateUserSocialNetwork(userSocialNetworkId, userSocialNetworkRequest);

        return ResponseEntity.ok(userSocialNetworkResponse);
    }

    @Operation(summary = "Remover uma rede social do usuário autenticado")
    @DeleteMapping("/{userSocialNetworkId}")
    public ResponseEntity<Void> deleteUserSocialNetwork(@PathVariable Long userSocialNetworkId){

        userSocialNetworkService.deleteUserSocialNetwork(userSocialNetworkId);

        return ResponseEntity.noContent().build();
    }
}
