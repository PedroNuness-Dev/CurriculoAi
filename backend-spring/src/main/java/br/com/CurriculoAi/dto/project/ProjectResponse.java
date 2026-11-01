package br.com.CurriculoAi.dto.project;

import br.com.CurriculoAi.dto.ability.AbilityResponse;

import java.util.Set;

public record ProjectResponse(
        Long projectId,
        String title,
        String description,
        String link,
        Set<AbilityResponse> abilities
) {
}
