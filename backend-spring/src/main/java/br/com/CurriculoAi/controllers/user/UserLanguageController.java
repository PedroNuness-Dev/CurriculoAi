package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserLanguageRequest;
import br.com.CurriculoAi.dto.user.UserLanguageResponse;
import br.com.CurriculoAi.services.user.UserLanguageService;
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
@RequestMapping("/api/users/languages")
@Tag(name = "User Language Controller")
@RequiredArgsConstructor
public class UserLanguageController {

    private final UserLanguageService userLanguageService;

    @Operation(summary = "Cadastrar idiomas do usuário autenticado")
    @PostMapping
    public ResponseEntity<List<UserLanguageResponse>> registerLanguages(@RequestBody List<@Valid UserLanguageRequest> userLanguageRequests){

        List<UserLanguageResponse> userLanguageResponse = userLanguageService.registerLanguages(userLanguageRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(userLanguageResponse);
    }

    @Operation(summary = "Listar os idiomas do usuário autenticado")
    @GetMapping
    public ResponseEntity<List<UserLanguageResponse>> getUserLanguagesOfUser(){

        List<UserLanguageResponse> userLanguageResponses = userLanguageService.getUserLanguagesOfUser();

        return ResponseEntity.ok(userLanguageResponses);
    }

    @Operation(summary = "Buscar um idioma do usuário autenticado pelo id")
    @GetMapping("/{userLanguageId}")
    public ResponseEntity<UserLanguageResponse> getUserLanguageById(@PathVariable Long userLanguageId){

        UserLanguageResponse userLanguageResponse = userLanguageService.getUserLanguageById(userLanguageId);

        return ResponseEntity.ok(userLanguageResponse);
    }

    @Operation(summary = "Atualizar um idioma do usuário autenticado")
    @PutMapping("/{userLanguageId}")
    public ResponseEntity<UserLanguageResponse> updateUserLanguage(
            @PathVariable Long userLanguageId,
            @RequestBody @Valid UserLanguageRequest userLanguageRequest){

        UserLanguageResponse userLanguageResponse = userLanguageService.updateUserLanguage(userLanguageId, userLanguageRequest);

        return ResponseEntity.ok(userLanguageResponse);
    }

    @Operation(summary = "Remover um idioma do usuário autenticado")
    @DeleteMapping("/{userLanguageId}")
    public ResponseEntity<Void> deleteUserLanguage(@PathVariable Long userLanguageId){

        userLanguageService.deleteUserLanguage(userLanguageId);

        return ResponseEntity.noContent().build();
    }
}
