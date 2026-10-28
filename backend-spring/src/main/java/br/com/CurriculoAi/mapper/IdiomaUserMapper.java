package br.com.CurriculoAi.mapper;

import br.com.CurriculoAi.dto.response.IdiomaUserDtoResponse;
import br.com.CurriculoAi.models.IdiomasUser;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {IdiomaMapper.class})
public interface IdiomaUserMapper {


    IdiomaUserDtoResponse toDto(IdiomasUser idiomasUser);
}
