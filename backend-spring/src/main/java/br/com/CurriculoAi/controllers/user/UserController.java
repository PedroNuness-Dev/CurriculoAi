package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserFullContent;
import br.com.CurriculoAi.dto.user.UserRegisterRequest;
import br.com.CurriculoAi.dto.user.UserResponse;
import br.com.CurriculoAi.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Responsável por controlar as ações relacionadas aos usuários")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Cadastrar um usuário")
    @PostMapping
    public ResponseEntity<UserResponse> registerUserWithRoleCandidate(@RequestBody UserRegisterRequest userRegisterRequest){

        UserResponse userResponse = userService.registerUserWithRoleTypeCandidate(userRegisterRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @GetMapping("/full")
    public ResponseEntity<UserFullContent> getUserFullContentData(){

        UserFullContent userFullContent = userService.getUserFullContentData();

        return ResponseEntity.ok(userFullContent);
    }
}
