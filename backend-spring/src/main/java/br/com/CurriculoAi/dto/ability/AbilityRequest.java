package br.com.CurriculoAi.dto.ability;

import jakarta.validation.constraints.NotBlank;

public record AbilityRequest(
        @NotBlank String name
) {
}
