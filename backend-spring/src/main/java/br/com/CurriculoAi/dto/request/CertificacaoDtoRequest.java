package br.com.CurriculoAi.dto.request;

import java.time.LocalDate;

public record CertificacaoDtoRequest(
        String nomeCertificacao,
        Long id_instituicao,
        LocalDate dataConclusao,
        byte[] certificado
) {
}
