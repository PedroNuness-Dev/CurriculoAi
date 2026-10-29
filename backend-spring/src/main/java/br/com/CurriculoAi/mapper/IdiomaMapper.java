package br.com.CurriculoAi.mapper;

import br.com.CurriculoAi.dto.response.IdiomaDtoResponse;
import br.com.CurriculoAi.models.Language;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IdiomaMapper {

    IdiomaDtoResponse toDto(Language language);
}
