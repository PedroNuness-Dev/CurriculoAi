package br.com.CurriculoAi.dto.social_network;

import jakarta.validation.constraints.NotBlank;

public record SocialNetworkRequest(
        @NotBlank String name
) {
}
