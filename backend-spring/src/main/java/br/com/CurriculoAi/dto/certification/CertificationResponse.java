package br.com.CurriculoAi.dto.certification;

import br.com.CurriculoAi.dto.institution.InstitutionResponse;

import java.time.LocalDate;

public record CertificationResponse(
        Long certificationId,
        String name,
        LocalDate finishDate,
        byte[] certificate,
        Boolean inProgress,
        InstitutionResponse institution
) {
}
