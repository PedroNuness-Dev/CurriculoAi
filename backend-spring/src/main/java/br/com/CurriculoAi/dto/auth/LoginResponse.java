package br.com.CurriculoAi.dto.auth;

import java.time.Instant;

public record LoginResponse(
        String token,
        String type, // "Bearer"
        Instant expiresAt,
        Long userId,
        String name,
        String email
) {
}
