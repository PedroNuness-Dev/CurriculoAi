package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserLanguageRequest;
import br.com.CurriculoAi.dto.user.UserLanguageResponse;
import br.com.CurriculoAi.services.user.UserLanguageService;
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
@Tag(name = "Candidato - Idiomas", description = "Idiomas do candidato autenticado, com o nível de proficiência")
@RequestMapping("/api/users/languages")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CANDIDATO')")
public class UserLanguageController {

    private final UserLanguageService userLanguageService;

    @Operation(
            summary = "Cadastrar idiomas",
            description = "Cadastra um ou mais idiomas do catálogo com o nível (BASICO, INTERMEDIARIO, AVANCADO, FLUENTE ou NATIVO). Retorna 404 se algum idioma não existir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Idiomas cadastrados com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PostMapping
    public ResponseEntity<List<UserLanguageResponse>> registerLanguages(@RequestBody List<@Valid UserLanguageRequest> userLanguageRequests){

        List<UserLanguageResponse> userLanguageResponse = userLanguageService.registerLanguages(userLanguageRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(userLanguageResponse);
    }

    @Operation(
            summary = "Listar idiomas",
            description = "Retorna todos os idiomas do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de idiomas retornada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @GetMapping
    public ResponseEntity<List<UserLanguageResponse>> getUserLanguagesOfUser(){

        List<UserLanguageResponse> userLanguageResponses = userLanguageService.getUserLanguagesOfUser();

        return ResponseEntity.ok(userLanguageResponses);
    }

    @Operation(
            summary = "Buscar idioma por id",
            description = "Retorna um idioma do candidato autenticado. Retorna 404 se o id não existir ou pertencer a outro usuário."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Idioma encontrado"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @GetMapping("/{userLanguageId}")
    public ResponseEntity<UserLanguageResponse> getUserLanguageById(@Parameter(description = "Id do idioma do candidato") @PathVariable Long userLanguageId){

        UserLanguageResponse userLanguageResponse = userLanguageService.getUserLanguageById(userLanguageId);

        return ResponseEntity.ok(userLanguageResponse);
    }

    @Operation(
            summary = "Atualizar um idioma",
            description = "Atualiza um idioma do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Idioma atualizado com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PutMapping("/{userLanguageId}")
    public ResponseEntity<UserLanguageResponse> updateUserLanguage(
            @Parameter(description = "Id do idioma do candidato")
            @PathVariable Long userLanguageId,
            @RequestBody @Valid UserLanguageRequest userLanguageRequest){

        UserLanguageResponse userLanguageResponse = userLanguageService.updateUserLanguage(userLanguageId, userLanguageRequest);

        return ResponseEntity.ok(userLanguageResponse);
    }

    @Operation(
            summary = "Remover um idioma",
            description = "Remove um idioma do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Idioma removido com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @DeleteMapping("/{userLanguageId}")
    public ResponseEntity<Void> deleteUserLanguage(@Parameter(description = "Id do idioma do candidato") @PathVariable Long userLanguageId){

        userLanguageService.deleteUserLanguage(userLanguageId);

        return ResponseEntity.noContent().build();
    }
}
