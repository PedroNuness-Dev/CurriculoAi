package br.com.CurriculoAi.security;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "api.security.token")
public record TokenProperties(
        @NotBlank String secret,
        @NotBlank String issuer
) {
}
