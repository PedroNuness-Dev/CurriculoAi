package br.com.CurriculoAi.dto.response;

import java.time.LocalDate;

public record CertificacaoUserDtoResponse(
        Long id,
        InstituicaoDtoResponse instituicao,
        LocalDate dataConclusao,
        Boolean emAndamento
) {
}
