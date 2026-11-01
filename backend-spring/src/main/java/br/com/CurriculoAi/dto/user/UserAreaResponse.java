package br.com.CurriculoAi.dto.user;

import br.com.CurriculoAi.dto.area.AreaResponse;

public record UserAreaResponse(
        Long userId,
        String name,
        String email,
        AreaResponse area
) {
}
