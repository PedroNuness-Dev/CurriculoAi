package br.com.CurriculoAi.dto.response;

import br.com.CurriculoAi.enums.ModeloDeTrabalho;

import java.time.LocalDate;

public record DisponibilidadeUserDtoResponse(
        Long id,
        LocalDate disponibilidadeInicio,
        ModeloDeTrabalho modeloTrabalho
) {
}
