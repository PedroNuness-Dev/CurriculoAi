package br.com.CurriculoAi.dto.formation;

import br.com.CurriculoAi.dto.course.CourseResponse;
import br.com.CurriculoAi.enums.formation.FormationType;

import java.time.LocalDate;

public record FormationResponse(
        Long formationId,
        FormationType formationType,
        LocalDate startDate,
        LocalDate finishDate,
        Boolean inProgress,
        CourseResponse course
) {
}
