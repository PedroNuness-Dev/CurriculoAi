package br.com.CurriculoAi.mapper.address;

import br.com.CurriculoAi.dto.address.AddressEntityResponse;
import br.com.CurriculoAi.models.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressEntityResponse toDto(Address address);
}
