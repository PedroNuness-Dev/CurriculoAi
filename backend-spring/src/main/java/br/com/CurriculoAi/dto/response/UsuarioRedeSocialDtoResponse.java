package br.com.CurriculoAi.dto.response;

public record UsuarioRedeSocialDtoResponse(
        Long id,
        String link,
        RedeSocialDtoResponse redeSocial
) {
}
