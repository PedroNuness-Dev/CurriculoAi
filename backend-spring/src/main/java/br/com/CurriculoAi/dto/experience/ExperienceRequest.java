package br.com.CurriculoAi.dto.experience;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ExperienceRequest(
        @NotBlank
        String title,
        @NotBlank
        String description,
        @NotNull
        Long areaId,
        @NotNull
        Long enterpriseId,
        @NotNull
        LocalDate startDate,
        @NotBlank
        String position,
        LocalDate finishDate,
        Boolean workCurrent
) {
}
