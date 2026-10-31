package br.com.CurriculoAi.mapper.availability;

import br.com.CurriculoAi.dto.availability.AvailabilityResponse;
import br.com.CurriculoAi.models.Availability;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {

    AvailabilityResponse toDto(Availability availability);
}
