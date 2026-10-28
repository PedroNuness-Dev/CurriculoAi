package br.com.CurriculoAi.mapper;

import br.com.CurriculoAi.dto.response.IdiomaDtoResponse;
import br.com.CurriculoAi.models.Idioma;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IdiomaMapper {

    IdiomaDtoResponse toDto(Idioma idioma);
}
