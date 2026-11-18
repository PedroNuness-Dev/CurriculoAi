package br.com.CurriculoAi.controllers.language;

import br.com.CurriculoAi.dto.language.LanguageRequest;
import br.com.CurriculoAi.dto.language.LanguageResponse;
import br.com.CurriculoAi.services.language.LanguageService;
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
@Tag(name = "Catálogo - Idiomas", description = "Idiomas disponíveis para o candidato informar no perfil. Leitura liberada para usuários autenticados; escrita restrita ao perfil ADMIN.")
@RequestMapping("/api/languages")
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    @Operation(
            summary = "Cadastrar um idioma",
            description = "Cadastra um novo idioma no catálogo da aplicação. Requer o perfil ADMIN. Retorna 400 se já existir um idioma com o mesmo nome."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Idioma cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<LanguageResponse> createLanguage(@Valid @RequestBody LanguageRequest languageRequest) {

        LanguageResponse languageResponse = languageService.createLanguage(languageRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(languageResponse);
    }

    @Operation(
            summary = "Listar idiomas",
            description = "Retorna todos os idiomas cadastrados no catálogo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de idiomas retornada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<LanguageResponse>> findAllLanguages() {

        List<LanguageResponse> languages = languageService.findAllLanguages();

        return ResponseEntity.ok(languages);
    }

    @Operation(
            summary = "Buscar idioma por id",
            description = "Retorna o idioma correspondente ao id informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Idioma encontrado"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{languageId}")
    public ResponseEntity<LanguageResponse> getLanguageById(@Parameter(description = "Id do idioma") @PathVariable Long languageId) {

        LanguageResponse languageResponse = languageService.getLanguageById(languageId);

        return ResponseEntity.ok(languageResponse);
    }

    @Operation(
            summary = "Atualizar um idioma",
            description = "Atualiza os dados de um idioma do catálogo. Requer o perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Idioma atualizado com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{languageId}")
    public ResponseEntity<LanguageResponse> updateLanguage(@Parameter(description = "Id do idioma") @PathVariable Long languageId,
                                                           @Valid @RequestBody LanguageRequest languageRequest) {

        LanguageResponse languageResponse = languageService.updateLanguage(languageId, languageRequest);

        return ResponseEntity.ok(languageResponse);
    }
}