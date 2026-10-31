package br.com.CurriculoAi.mapper.course;

import br.com.CurriculoAi.dto.course.CourseResponse;
import br.com.CurriculoAi.models.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseResponse toDto(Course course);
}
