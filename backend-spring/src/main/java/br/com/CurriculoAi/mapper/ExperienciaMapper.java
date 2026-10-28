package br.com.CurriculoAi.mapper;

import br.com.CurriculoAi.DTO.response.ExperienciaUserDtoResponse;
import br.com.CurriculoAi.models.ExperienciaUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AreaUserMapper.class, EmpresaMapper.class})
public interface ExperienciaMapper {

    ExperienciaUserDtoResponse toDto(ExperienciaUser experienciaUser);
}
