package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserAddressResponse;
import br.com.CurriculoAi.services.user.UserAddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/users/address")
@Tag(name = "user Address Controller")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @PostMapping("/{zipCode}")
    public ResponseEntity<UserAddressResponse> registerAddressInUserByZipCode(
            @PathVariable
            @NotBlank(message = "O cep é obrigatório")
            @Pattern(regexp = "\\d{8}", message = "O cep deve conter exatamente 8 dígitos numéricos")
            String zipCode) {

        UserAddressResponse userAddressResponse = userAddressService.registerAddressInUserByZipCode(zipCode);

        return ResponseEntity.status(HttpStatus.CREATED).body(userAddressResponse);
    }
}