package br.com.CurriculoAi.dto.curriculum;

import java.time.LocalDateTime;

public record CurriculumResponse(
        Long curriculumId,
        String title,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
