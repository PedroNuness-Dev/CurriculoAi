package br.com.CurriculoAi.dto.formation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record FormationRequest(
        @NotNull
        Long courseId,
        @NotBlank
        String formationType,
        @NotNull
        LocalDate startDate,
        LocalDate finishDate
) {
}
