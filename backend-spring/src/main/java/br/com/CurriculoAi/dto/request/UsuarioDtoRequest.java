package br.com.CurriculoAi.dto.request;

public record UsuarioDtoRequest(
        String nome,
        String email,
        String senha
) {
}
