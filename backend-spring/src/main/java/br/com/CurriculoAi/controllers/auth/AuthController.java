package br.com.CurriculoAi.controllers.auth;

import br.com.CurriculoAi.dto.auth.LoginRequest;
import br.com.CurriculoAi.dto.auth.LoginResponse;
import br.com.CurriculoAi.services.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static br.com.CurriculoAi.config.OpenApiResponseRefs.*;

@RestController
@Tag(name = "Autenticação", description = "Login e emissão do token JWT usado nas demais requisições")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Autenticar usuário",
            description = "Valida e-mail e senha e retorna um token JWT do tipo Bearer, com a data de expiração e os dados básicos do usuário."
    )
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticado com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED)
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest){

        LoginResponse loginResponse = authService.authenticateUser(loginRequest);

        return ResponseEntity.ok(loginResponse);
    }
}
