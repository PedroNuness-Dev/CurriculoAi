package br.com.CurriculoAi.dto.institution;

import jakarta.validation.constraints.NotBlank;

public record InstitutionRequest(
        @NotBlank String name
) {
}
