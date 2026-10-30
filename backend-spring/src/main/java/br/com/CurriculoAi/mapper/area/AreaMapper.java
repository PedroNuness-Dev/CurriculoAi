package br.com.CurriculoAi.mapper.area;

import br.com.CurriculoAi.dto.area.AreaResponse;
import br.com.CurriculoAi.models.Area;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AreaMapper {

        AreaResponse toDTO(Area area);
}
