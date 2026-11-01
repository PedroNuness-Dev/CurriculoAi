package br.com.CurriculoAi.mapper.ability;

import br.com.CurriculoAi.dto.ability.AbilityResponse;
import br.com.CurriculoAi.models.Ability;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AbilityMapper {

    AbilityResponse toDto(Ability ability);
}
