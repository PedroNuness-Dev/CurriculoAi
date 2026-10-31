package br.com.CurriculoAi.dto.availability;

import br.com.CurriculoAi.enums.availability.WorkModel;

import java.time.LocalDate;

public record AvailabilityResponse(
        Long availabilityId,
        LocalDate startAvailability,
        WorkModel workModel
) {
}
