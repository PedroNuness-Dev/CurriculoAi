package br.com.CurriculoAi.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserLanguageRequest(
        @NotNull
        Long languageId,
        @NotBlank
        String languageLevel
) {
}
