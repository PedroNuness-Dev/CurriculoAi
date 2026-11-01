package br.com.CurriculoAi.dto.curriculum;

import java.time.LocalDateTime;

public record CurriculumResponse(
        Long curriculumId,
        String title,
        byte[] curriculum,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
