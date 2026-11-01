package br.com.CurriculoAi.dto.area;

import jakarta.validation.constraints.NotBlank;

public record AreaRequest(
        @NotBlank String name
) {
}
