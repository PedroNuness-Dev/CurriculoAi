package br.com.CurriculoAi.mapper.enterprise;

import br.com.CurriculoAi.dto.enterprise.EnterpriseResponse;
import br.com.CurriculoAi.models.Enterprise;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnterpriseMapper {

    EnterpriseResponse toDto(Enterprise enterprise);
}
