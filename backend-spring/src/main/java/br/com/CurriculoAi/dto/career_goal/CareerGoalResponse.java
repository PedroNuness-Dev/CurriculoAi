package br.com.CurriculoAi.dto.career_goal;

import br.com.CurriculoAi.dto.area.AreaResponse;

import java.time.LocalDateTime;

public record CareerGoalResponse(
        Long careerGoalId,
        String goalType,
        AreaResponse area,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
