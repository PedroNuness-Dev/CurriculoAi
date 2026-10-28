package br.com.CurriculoAi.dto.response;

import java.time.LocalDate;

public record FormacaoUserDtoResponse(
        Long id,
        CursoDtoResponse curso,
        String tipoFormacao,
        LocalDate dataInicio,
        LocalDate dataConclusao,
        Boolean emAndamento
) {
}
