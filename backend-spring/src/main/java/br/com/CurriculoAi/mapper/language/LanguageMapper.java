package br.com.CurriculoAi.mapper.language;

import br.com.CurriculoAi.dto.language.LanguageResponse;
import br.com.CurriculoAi.models.Language;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguageMapper {

    LanguageResponse toDto(Language language);
}
