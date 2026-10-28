package br.com.CurriculoAi.mapper;

import br.com.CurriculoAi.dto.response.CursoDtoResponse;
import br.com.CurriculoAi.models.Curso;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CursoMapper {

    CursoDtoResponse toDto(Curso curso);
}
