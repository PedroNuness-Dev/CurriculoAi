package br.com.CurriculoAi.dto.certification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CertificationRequest(
        @NotBlank
        String name,
        @NotNull
        Long institutionId,
        LocalDate finishDate,
        @NotNull
        byte[] certificate
) {
}
