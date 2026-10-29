package br.com.CurriculoAi.dto.response;

import br.com.CurriculoAi.enums.availability.WorkModel;

import java.time.LocalDate;

public record DisponibilidadeUserDtoResponse(
        Long id,
        LocalDate disponibilidadeInicio,
        WorkModel modeloTrabalho
) {
}
