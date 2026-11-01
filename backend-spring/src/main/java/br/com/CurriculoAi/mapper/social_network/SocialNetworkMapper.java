package br.com.CurriculoAi.mapper.social_network;

import br.com.CurriculoAi.dto.social_network.SocialNetworkResponse;
import br.com.CurriculoAi.models.SocialNetwork;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SocialNetworkMapper {

    SocialNetworkResponse toDto(SocialNetwork socialNetwork);
}
