package br.com.CurriculoAi.dto.user;

import br.com.CurriculoAi.dto.language.LanguageResponse;
import br.com.CurriculoAi.enums.language.LanguageLevel;

public record UserLanguageResponse(
        Long userLanguageId,
        LanguageLevel level,
        LanguageResponse language
) {
}
