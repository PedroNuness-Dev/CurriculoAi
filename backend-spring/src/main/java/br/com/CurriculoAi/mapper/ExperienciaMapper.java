package br.com.CurriculoAi.mapper;

import br.com.CurriculoAi.dto.response.ExperienciaUserDtoResponse;
import br.com.CurriculoAi.models.Experience;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AreaUserMapper.class, EmpresaMapper.class})
public interface ExperienciaMapper {

    ExperienciaUserDtoResponse toDto(Experience experience);
}
