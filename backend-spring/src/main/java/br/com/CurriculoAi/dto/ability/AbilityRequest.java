package br.com.CurriculoAi.dto.ability;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AbilityRequest(
        @NotBlank @Size(max = 100) String name,
        String category
) {
    public AbilityRequest(String name) { this(name, "HARD"); }
}
