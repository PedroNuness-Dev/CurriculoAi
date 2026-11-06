package br.com.CurriculoAi.client.address;

import br.com.CurriculoAi.dto.address.AddressClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "AddressClient", url = "https://viacep.com.br/ws")
public interface AddressClient {

    @GetMapping("/{zipCode}/json")
    AddressClientResponse getDetailsByZipCode(@PathVariable("zipCode") String zipCode);
}
