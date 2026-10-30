package br.com.CurriculoAi.mapper.course;

import br.com.CurriculoAi.dto.response.CursoDtoResponse;
import br.com.CurriculoAi.models.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CursoDtoResponse toDto(Course course);
}
