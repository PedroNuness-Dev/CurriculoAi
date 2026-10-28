package br.com.CurriculoAi.mapper;

import br.com.CurriculoAi.dto.response.InstituicaoDtoResponse;
import br.com.CurriculoAi.models.Instituicao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InstituicaoMapper {

    InstituicaoDtoResponse toDto(Instituicao instituicao);
}
