package br.com.CurriculoAi.mapper;

import br.com.CurriculoAi.dto.response.EmpresaDtoResponse;
import br.com.CurriculoAi.models.Empresa;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    EmpresaDtoResponse toDto(Empresa empresa);
}
