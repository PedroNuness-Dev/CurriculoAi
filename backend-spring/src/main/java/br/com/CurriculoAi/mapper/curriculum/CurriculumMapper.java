package br.com.CurriculoAi.mapper.curriculum;

import br.com.CurriculoAi.dto.curriculum.CurriculumResponse;
import br.com.CurriculoAi.mapper.user.UserMapper;
import br.com.CurriculoAi.models.Curriculum;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CurriculumMapper {

    CurriculumResponse toDto(Curriculum curriculum);
}
