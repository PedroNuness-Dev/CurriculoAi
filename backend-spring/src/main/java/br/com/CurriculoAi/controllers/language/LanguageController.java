package br.com.CurriculoAi.controllers.language;

import br.com.CurriculoAi.dto.language.LanguageRequest;
import br.com.CurriculoAi.dto.language.LanguageResponse;
import br.com.CurriculoAi.services.language.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
@Tag(name = "Language Controller", description = "Responsável por controlar as ações relacionadas aos idiomas do sistema")
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    @Operation(summary = "Cadastrar um idioma")
    @PostMapping("/create")
    public ResponseEntity<LanguageResponse> createLanguage(@Valid @RequestBody LanguageRequest languageRequest) {

        LanguageResponse languageResponse = languageService.createLanguage(languageRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(languageResponse);
    }

    @Operation(summary = "Listar todos os idiomas")
    @GetMapping
    public ResponseEntity<List<LanguageResponse>> findAllLanguages() {

        List<LanguageResponse> languages = languageService.findAllLanguages();

        return ResponseEntity.ok(languages);
    }

    @Operation(summary = "Buscar um idioma pelo id")
    @GetMapping("/{languageId}")
    public ResponseEntity<LanguageResponse> getLanguageById(@PathVariable Long languageId) {

        LanguageResponse languageResponse = languageService.getLanguageById(languageId);

        return ResponseEntity.ok(languageResponse);
    }

    @Operation(summary = "Atualizar um idioma")
    @PutMapping("/{languageId}")
    public ResponseEntity<LanguageResponse> updateLanguage(@PathVariable Long languageId,
                                                           @Valid @RequestBody LanguageRequest languageRequest) {

        LanguageResponse languageResponse = languageService.updateLanguage(languageId, languageRequest);

        return ResponseEntity.ok(languageResponse);
    }
}