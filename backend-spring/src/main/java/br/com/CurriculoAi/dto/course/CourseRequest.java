package br.com.CurriculoAi.dto.course;

import jakarta.validation.constraints.NotBlank;

public record CourseRequest(
        @NotBlank String name
) {
}
