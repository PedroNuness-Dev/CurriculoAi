package br.com.CurriculoAi.mapper.institution;

import br.com.CurriculoAi.dto.institution.InstitutionResponse;
import br.com.CurriculoAi.models.Institution;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InstitutionMapper {

    InstitutionResponse toDto(Institution institution);
}
