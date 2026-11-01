package br.com.CurriculoAi.mapper.formation;

import br.com.CurriculoAi.dto.formation.FormationResponse;
import br.com.CurriculoAi.mapper.course.CourseMapper;
import br.com.CurriculoAi.models.Formation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CourseMapper.class})
public interface FormationMapper {

    FormationResponse toDto(Formation formation);
}
