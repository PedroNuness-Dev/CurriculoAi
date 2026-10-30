package br.com.CurriculoAi.mapper.availability;

import br.com.CurriculoAi.dto.response.DisponibilidadeUserDtoResponse;
import br.com.CurriculoAi.models.Availability;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {

    DisponibilidadeUserDtoResponse toDto(Availability availability);
}
