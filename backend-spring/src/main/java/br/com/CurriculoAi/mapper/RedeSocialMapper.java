package br.com.CurriculoAi.mapper;

import br.com.CurriculoAi.dto.response.RedeSocialDtoResponse;
import br.com.CurriculoAi.models.SocialNetwork;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RedeSocialMapper {

    RedeSocialDtoResponse toDto(SocialNetwork socialNetwork);
}
