package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserAbilityResponse;
import br.com.CurriculoAi.services.user.UserAbilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/users/abilities")
@Tag(name = "user Ability Controller")
@RequiredArgsConstructor
public class UserAbilityController {

    private final UserAbilityService userAbilityService;

    @Operation(summary = "Adicionar habilidades ao usuário autenticado (mantém as que já existem)")
    @PostMapping
    public ResponseEntity<UserAbilityResponse> registerUserAbilities(@RequestBody List<@NotNull Long> abilitiesIds){

        UserAbilityResponse userAbilityResponse = userAbilityService.registerUserAbilities(abilitiesIds);

        return ResponseEntity.status(HttpStatus.CREATED).body(userAbilityResponse);
    }

    @Operation(summary = "Listar as habilidades do usuário autenticado")
    @GetMapping
    public ResponseEntity<UserAbilityResponse> getAbilitiesOfUser(){

        UserAbilityResponse userAbilityResponse = userAbilityService.getAbilitiesOfUser();

        return ResponseEntity.ok(userAbilityResponse);
    }

    @Operation(summary = "Substituir todas as habilidades do usuário autenticado")
    @PutMapping
    public ResponseEntity<UserAbilityResponse> replaceUserAbilities(@RequestBody List<@NotNull Long> abilitiesIds){

        UserAbilityResponse userAbilityResponse = userAbilityService.replaceUserAbilities(abilitiesIds);

        return ResponseEntity.ok(userAbilityResponse);
    }

    @Operation(summary = "Remover todas as habilidades do usuário autenticado")
    @DeleteMapping
    public ResponseEntity<Void> removeAllUserAbilities(){

        userAbilityService.removeAllUserAbilities();

        return ResponseEntity.noContent().build();
    }
}
