package br.com.CurriculoAi.dto.language;

import br.com.CurriculoAi.enums.language.LanguageEnum;

public record LanguageResponse(
        Long languageId,
        LanguageEnum name
) {
}
