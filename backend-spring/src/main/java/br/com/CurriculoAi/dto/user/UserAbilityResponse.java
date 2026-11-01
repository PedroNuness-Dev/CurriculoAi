package br.com.CurriculoAi.dto.user;

import br.com.CurriculoAi.dto.ability.AbilityResponse;

import java.util.List;

public record UserAbilityResponse(
        Long userId,
        String name,
        String email,
        List<AbilityResponse> abilities
) {
}
