package br.com.CurriculoAi.dto.curriculum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CurriculumRequest(
        @NotBlank
        String title,
        @NotNull
        byte[] curriculum
) {
}
