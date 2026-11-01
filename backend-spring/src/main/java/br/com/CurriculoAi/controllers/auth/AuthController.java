package br.com.CurriculoAi.controllers.auth;

import br.com.CurriculoAi.dto.auth.LoginRequest;
import br.com.CurriculoAi.dto.auth.LoginResponse;
import br.com.CurriculoAi.services.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest){

        LoginResponse loginResponse = authService.authenticateUser(loginRequest);

        return ResponseEntity.ok(loginResponse);
    }
}
