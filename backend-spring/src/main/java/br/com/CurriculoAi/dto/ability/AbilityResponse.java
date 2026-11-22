package br.com.CurriculoAi.dto.ability;

public record AbilityResponse(
        Long abilityId,
        String name,
        String category
) {
    public AbilityResponse(Long abilityId, String name) { this(abilityId, name, "HARD"); }
}
