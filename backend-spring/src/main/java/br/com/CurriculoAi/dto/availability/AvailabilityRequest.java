package br.com.CurriculoAi.dto.availability;

import java.time.LocalDate;

public record AvailabilityRequest(
        LocalDate startAvailability,
        String workModel
) {
}
