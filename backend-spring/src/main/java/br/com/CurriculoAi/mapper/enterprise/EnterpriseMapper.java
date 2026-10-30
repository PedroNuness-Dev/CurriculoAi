package br.com.CurriculoAi.mapper.enterprise;

import br.com.CurriculoAi.dto.response.EmpresaDtoResponse;
import br.com.CurriculoAi.models.Enterprise;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnterpriseMapper {

    EmpresaDtoResponse toDto(Enterprise enterprise);
}
