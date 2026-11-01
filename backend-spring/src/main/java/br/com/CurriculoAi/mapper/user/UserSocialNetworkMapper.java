package br.com.CurriculoAi.mapper.user;

import br.com.CurriculoAi.dto.user.UserSocialNetworkResponse;
import br.com.CurriculoAi.mapper.social_network.SocialNetworkMapper;
import br.com.CurriculoAi.models.UserSocialNetwork;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, SocialNetworkMapper.class})
public interface UserSocialNetworkMapper {

    UserSocialNetworkResponse toDto(UserSocialNetwork userSocialNetwork);
}
