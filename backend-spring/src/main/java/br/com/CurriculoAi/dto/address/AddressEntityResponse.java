package br.com.CurriculoAi.dto.address;

public record AddressEntityResponse(
        Long addressId,
        String zipCode,
        String street,
        String complement,
        String unit,
        String neighborhood,
        String city,
        String stateAbbreviation,
        String state,
        String region
) {
}
