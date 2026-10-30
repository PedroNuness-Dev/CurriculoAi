package br.com.CurriculoAi.mapper;

import br.com.CurriculoAi.dto.response.ExperienciaUserDtoResponse;
import br.com.CurriculoAi.mapper.area.AreaMapper;
import br.com.CurriculoAi.models.Experience;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AreaMapper.class, EmpresaMapper.class})
public interface ExperienciaMapper {

    ExperienciaUserDtoResponse toDto(Experience experience);
}
