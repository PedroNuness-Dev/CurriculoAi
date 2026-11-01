package br.com.CurriculoAi.dto.user;

public record UserRegisterRequest(
        String name,
        String email,
        String password
) {
}
