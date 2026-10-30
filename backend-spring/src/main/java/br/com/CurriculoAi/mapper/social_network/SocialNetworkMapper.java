package br.com.CurriculoAi.mapper.social_network;

import br.com.CurriculoAi.dto.response.RedeSocialDtoResponse;
import br.com.CurriculoAi.models.SocialNetwork;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SocialNetworkMapper {

    RedeSocialDtoResponse toDto(SocialNetwork socialNetwork);
}
