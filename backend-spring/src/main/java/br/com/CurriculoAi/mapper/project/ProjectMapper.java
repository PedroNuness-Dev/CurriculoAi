package br.com.CurriculoAi.mapper.project;

import br.com.CurriculoAi.dto.project.ProjectResponse;
import br.com.CurriculoAi.dto.response.ProjetoDtoResponse;
import br.com.CurriculoAi.models.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toDto(Project project);
}
