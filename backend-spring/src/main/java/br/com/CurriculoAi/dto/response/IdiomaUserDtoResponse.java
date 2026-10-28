package br.com.CurriculoAi.dto.response;


public record IdiomaUserDtoResponse(
        Long id,
        String nivel,
        IdiomaDtoResponse idioma
) {
}
