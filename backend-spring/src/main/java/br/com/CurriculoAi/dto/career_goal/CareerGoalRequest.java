package br.com.CurriculoAi.dto.career_goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CareerGoalRequest(
        @NotBlank
        String goalType,
        @NotNull
        Long areaId,
        @NotBlank
        String description
) {
}
