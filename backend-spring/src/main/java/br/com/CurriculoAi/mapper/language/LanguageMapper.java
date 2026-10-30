package br.com.CurriculoAi.mapper.language;

import br.com.CurriculoAi.dto.response.IdiomaDtoResponse;
import br.com.CurriculoAi.models.Language;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguageMapper {

    IdiomaDtoResponse toDto(Language language);
}
