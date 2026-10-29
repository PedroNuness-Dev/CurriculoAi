package br.com.CurriculoAi.mapper;

import br.com.CurriculoAi.dto.response.ProjetoDtoResponse;
import br.com.CurriculoAi.models.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjetoMapper {

    ProjetoDtoResponse toDto(Project project);
}
