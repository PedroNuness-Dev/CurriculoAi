package br.com.CurriculoAi.dto.language;

import jakarta.validation.constraints.NotBlank;

public record LanguageRequest(
        @NotBlank String name
) {
}
