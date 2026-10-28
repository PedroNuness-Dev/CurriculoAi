package br.com.CurriculoAi.dto.response;

import java.time.LocalDate;

public record ExperienciaUserDtoResponse(
        Long id,
        AreaDTOResponse area,
        EmpresaDtoResponse empresa,
        LocalDate dataInicio,
        String cargo,
        LocalDate dataFim,
        String titulo,
        String descricao,
        Boolean trabalhoAtual
) {
}
