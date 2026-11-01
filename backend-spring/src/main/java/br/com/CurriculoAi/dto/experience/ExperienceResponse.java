package br.com.CurriculoAi.dto.experience;

import br.com.CurriculoAi.dto.area.AreaResponse;
import br.com.CurriculoAi.dto.enterprise.EnterpriseResponse;

import java.time.LocalDate;

public record ExperienceResponse(
        Long experienceId,
        String title,
        String description,
        String position,
        LocalDate startDate,
        LocalDate finishDate,
        Boolean workCurrent,
        AreaResponse area,
        EnterpriseResponse enterprise
) {
}
