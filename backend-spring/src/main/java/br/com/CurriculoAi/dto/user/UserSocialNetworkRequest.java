package br.com.CurriculoAi.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserSocialNetworkRequest(
        @NotNull
        Long socialNetworkId,
        @NotBlank
        String link
) {
}
