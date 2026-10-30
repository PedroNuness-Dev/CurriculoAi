package br.com.CurriculoAi.mapper.institution;

import br.com.CurriculoAi.dto.response.InstituicaoDtoResponse;
import br.com.CurriculoAi.models.Institution;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InstitutionMapper {

    InstituicaoDtoResponse toDto(Institution institution);
}
