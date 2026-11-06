package br.com.CurriculoAi.services.address;

import br.com.CurriculoAi.client.address.AddressClient;
import br.com.CurriculoAi.dto.address.AddressClientResponse;
import br.com.CurriculoAi.dto.address.AddressEntityResponse;
import br.com.CurriculoAi.mapper.address.AddressMapper;
import br.com.CurriculoAi.models.Address;
import br.com.CurriculoAi.repositories.address.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressClient addressClient;
    private final AddressMapper addressMapper;

    @Transactional
    public Address getDetailsByZipCode(String zipCode){

        log.info("Iniciando busca de detalhes de endereço pelo cep [{}]", zipCode);

        AddressClientResponse addressClientResponse = addressClient
                .getDetailsByZipCode(zipCode);

        if (addressClientResponse == null || addressClientResponse.isInvalid()) {
            log.warn("Cep inválido ou não encontrado na API externa [{}]", zipCode);
            throw new IllegalArgumentException("Cep inválido: " + zipCode);
        }

        log.info("Detalhes do cep [{}] obtidos com sucesso na API externa", zipCode);

        return addressRepository.findByZipCode(addressClientResponse.zipCode())
                .map(address -> {
                    log.info("Endereço já cadastrado no banco para o cep [{}], reaproveitando registro id [{}]",
                            address.getZipCode(), address.getAddressId());
                    return address;
                })
                .orElseGet(() -> saveNewAddress(addressClientResponse));
    }

    private Address saveNewAddress(AddressClientResponse addressClientResponse){

        log.info("Endereço não encontrado no banco, iniciando cadastro do cep [{}]", addressClientResponse.zipCode());

        if (addressRepository.existsByZipCode(addressClientResponse.zipCode())){
            log.warn("Tentativa de cadastrar cep já existente no banco [{}]", addressClientResponse.zipCode());
            throw new IllegalArgumentException("Cep já cadastrado no banco: "+ addressClientResponse.zipCode());
        }

        Address address = Address.builder()
                .zipCode(blankToNull(addressClientResponse.zipCode()))
                .city(blankToNull(addressClientResponse.city()))
                .complement(blankToNull(addressClientResponse.complement()))
                .neighborhood(blankToNull(addressClientResponse.neighborhood()))
                .region(blankToNull(addressClientResponse.region()))
                .state(blankToNull(addressClientResponse.state()))
                .stateAbbreviation(blankToNull(addressClientResponse.stateAbbreviation()))
                .street(blankToNull(addressClientResponse.street()))
                .unit(blankToNull(addressClientResponse.unit()))
                .build();

        Address savedAddress = addressRepository.save(address);

        log.info("Endereço cadastrado com sucesso, id [{}] e cep [{}]",
                savedAddress.getAddressId(), savedAddress.getZipCode());

        return savedAddress;
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    public AddressEntityResponse toEntityResponse(Address address){
        log.debug("Convertendo endereço id [{}] para AddressEntityResponse", address.getAddressId());
        return addressMapper.toDto(address);
    }
}