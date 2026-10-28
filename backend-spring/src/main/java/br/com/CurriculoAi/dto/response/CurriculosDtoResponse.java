package br.com.CurriculoAi.dto.response;

import java.time.LocalDateTime;

public record CurriculosDtoResponse(
        String area,
        byte[] curriculo,
        LocalDateTime dataCriacao,
        LocalDateTime ultimaAtualizacao
) {
}
