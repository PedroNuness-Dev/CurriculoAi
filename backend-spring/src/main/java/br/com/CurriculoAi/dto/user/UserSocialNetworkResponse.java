package br.com.CurriculoAi.dto.user;

import br.com.CurriculoAi.dto.social_network.SocialNetworkResponse;

public record UserSocialNetworkResponse(
        Long userSocialNetworkId,
        String link,
        SocialNetworkResponse socialNetwork

) {
}
