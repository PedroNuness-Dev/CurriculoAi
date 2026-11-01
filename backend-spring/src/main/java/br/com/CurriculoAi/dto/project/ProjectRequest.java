package br.com.CurriculoAi.dto.project;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ProjectRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String link,
        List<Long> abilitiesIds
) {
}
