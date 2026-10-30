package br.com.CurriculoAi.mapper.formation;

import br.com.CurriculoAi.dto.response.FormacaoUserDtoResponse;
import br.com.CurriculoAi.mapper.course.CourseMapper;
import br.com.CurriculoAi.models.Formation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CourseMapper.class})
public interface FormationMapper {

    FormacaoUserDtoResponse toDto(Formation formation);
}
