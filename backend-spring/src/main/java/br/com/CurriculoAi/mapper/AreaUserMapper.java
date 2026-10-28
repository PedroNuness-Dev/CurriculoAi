package br.com.CurriculoAi.mapper;

import br.com.CurriculoAi.DTO.response.AreaDTOResponse;
import br.com.CurriculoAi.models.Area;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AreaUserMapper {

        AreaDTOResponse toDTO(Area area);
}
