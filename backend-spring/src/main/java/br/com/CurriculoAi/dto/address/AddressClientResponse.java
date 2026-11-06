package br.com.CurriculoAi.dto.address;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AddressClientResponse(
        @JsonProperty("cep") String zipCode,
        @JsonProperty("logradouro") String street,
        @JsonProperty("complemento") String complement,
        @JsonProperty("unidade") String unit,
        @JsonProperty("bairro") String neighborhood,
        @JsonProperty("localidade") String city,
        @JsonProperty("uf") String stateAbbreviation,
        @JsonProperty("estado") String state,
        @JsonProperty("regiao") String region,
        @JsonProperty("erro") String error
) {

    public boolean isInvalid() {
        return "true".equalsIgnoreCase(error);
    }
}