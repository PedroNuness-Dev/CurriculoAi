package br.com.CurriculoAi.dto.request;

import java.time.LocalDate;

public record DisponibilidadeDtoRequest(
        LocalDate disponibilidadeInicio,
        String modeloTrabalho
) {
}
