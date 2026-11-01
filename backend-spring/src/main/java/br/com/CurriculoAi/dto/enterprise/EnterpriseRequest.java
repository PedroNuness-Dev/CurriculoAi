package br.com.CurriculoAi.dto.enterprise;

import jakarta.validation.constraints.NotBlank;

public record EnterpriseRequest(
        @NotBlank
        String name
) {
}
