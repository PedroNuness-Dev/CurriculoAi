package br.com.CurriculoAi.dto.response;

public record UsuarioTokenIdentResponseDto(
        Long id,
        String nome, //Nome do usuário
        String token //Token de identificação
) {
}
