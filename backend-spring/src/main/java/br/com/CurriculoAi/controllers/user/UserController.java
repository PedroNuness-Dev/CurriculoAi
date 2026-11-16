package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserFullContent;
import br.com.CurriculoAi.dto.user.UserRegisterRequest;
import br.com.CurriculoAi.dto.user.UserResponse;
import br.com.CurriculoAi.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static br.com.CurriculoAi.config.OpenApiResponseRefs.*;

@RestController
@Tag(name = "Usuários", description = "Cadastro de usuários e consulta do perfil completo")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Cadastrar usuário",
            description = "Cria uma conta com o perfil CANDIDATO. Endpoint público. Retorna 400 se o e-mail já estiver em uso."
    )
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
    })
    @PostMapping
    public ResponseEntity<UserResponse> registerUserWithRoleCandidate(@RequestBody @Valid UserRegisterRequest userRegisterRequest){

        UserResponse userResponse = userService.registerUserWithRoleTypeCandidate(userRegisterRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @Operation(
            summary = "Buscar perfil completo",
            description = "Retorna todos os dados do usuário autenticado (área, endereço, formações, experiências, certificações, idiomas, projetos, redes sociais, habilidades, disponibilidade e objetivo de carreira). É a base usada na geração do currículo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PreAuthorize("hasRole('CANDIDATO')")
    @GetMapping("/me/profile")
    public ResponseEntity<UserFullContent> getUserFullContentData(){

        UserFullContent userFullContent = userService.getUserFullContentData();

        return ResponseEntity.ok(userFullContent);
    }
}
