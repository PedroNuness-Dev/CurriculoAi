package br.com.CurriculoAi.dto.auth;

import java.time.Instant;

public record TokenDetails(
        String token,
        Instant expiration
) {
}
