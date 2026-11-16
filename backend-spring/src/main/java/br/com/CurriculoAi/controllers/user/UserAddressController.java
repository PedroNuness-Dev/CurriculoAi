package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserAddressResponse;
import br.com.CurriculoAi.services.user.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static br.com.CurriculoAi.config.OpenApiResponseRefs.*;

@Validated
@RestController
@Tag(name = "Candidato - Endereço", description = "Endereço do candidato autenticado, obtido a partir do CEP")
@RequestMapping("/api/users/address")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CANDIDATO')")
public class UserAddressController {

    private final UserAddressService userAddressService;

    @Operation(
            summary = "Cadastrar endereço pelo CEP",
            description = "Consulta o CEP no serviço externo e vincula o endereço ao candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Endereço cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND),
            @ApiResponse(responseCode = "502", ref = BAD_GATEWAY),
            @ApiResponse(responseCode = "503", ref = SERVICE_UNAVAILABLE)
    })
    @PostMapping("/{zipCode}")
    public ResponseEntity<UserAddressResponse> registerAddressInUserByZipCode(
            @Parameter(description = "CEP com 8 dígitos numéricos, sem traço (ex.: 01001000)")
            @PathVariable
            @NotBlank(message = "O cep é obrigatório")
            @Pattern(regexp = "\\d{8}", message = "O cep deve conter exatamente 8 dígitos numéricos")
            String zipCode) {

        UserAddressResponse userAddressResponse = userAddressService.registerAddressInUserByZipCode(zipCode);

        return ResponseEntity.status(HttpStatus.CREATED).body(userAddressResponse);
    }

    @Operation(
            summary = "Buscar endereço",
            description = "Retorna o endereço vinculado ao candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Endereço retornado com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @GetMapping
    public ResponseEntity<UserAddressResponse> getAddressOfUser(){

        UserAddressResponse userAddressResponse = userAddressService.getAddressOfUser();

        return ResponseEntity.ok(userAddressResponse);
    }

    @Operation(
            summary = "Atualizar endereço pelo CEP",
            description = "Consulta o novo CEP no serviço externo e substitui o endereço do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND),
            @ApiResponse(responseCode = "502", ref = BAD_GATEWAY),
            @ApiResponse(responseCode = "503", ref = SERVICE_UNAVAILABLE)
    })
    @PutMapping("/{zipCode}")
    public ResponseEntity<UserAddressResponse> updateAddressOfUserByZipCode(
            @Parameter(description = "CEP com 8 dígitos numéricos, sem traço (ex.: 01001000)")
            @PathVariable
            @NotBlank(message = "O cep é obrigatório")
            @Pattern(regexp = "\\d{8}", message = "O cep deve conter exatamente 8 dígitos numéricos")
            String zipCode) {

        UserAddressResponse userAddressResponse = userAddressService.updateAddressOfUserByZipCode(zipCode);

        return ResponseEntity.ok(userAddressResponse);
    }

    @Operation(
            summary = "Remover endereço",
            description = "Remove o vínculo do endereço com o candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Endereço desvinculado com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @DeleteMapping
    public ResponseEntity<Void> removeAddressOfUser(){

        userAddressService.removeAddressOfUser();

        return ResponseEntity.noContent().build();
    }
}
