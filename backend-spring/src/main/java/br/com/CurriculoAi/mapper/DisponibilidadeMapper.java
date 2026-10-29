package br.com.CurriculoAi.mapper;

import br.com.CurriculoAi.dto.response.DisponibilidadeUserDtoResponse;
import br.com.CurriculoAi.models.Availability;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DisponibilidadeMapper {

    DisponibilidadeUserDtoResponse toDto(Availability availability);
}
