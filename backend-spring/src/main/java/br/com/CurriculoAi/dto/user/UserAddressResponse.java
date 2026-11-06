package br.com.CurriculoAi.dto.user;

import br.com.CurriculoAi.dto.address.AddressEntityResponse;

public record UserAddressResponse(
        Long userId,
        String name,
        String email,
        AddressEntityResponse address
) {
}
