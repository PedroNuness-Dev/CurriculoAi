package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserAbilityResponse;
import br.com.CurriculoAi.services.user.UserAbilityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/users/abilities")
@Tag(name = "user Ability Controller")
@RequiredArgsConstructor
public class UserAbilityController {

    private final UserAbilityService userAbilityService;

    @PostMapping
    public ResponseEntity<UserAbilityResponse> registerUserAbilities(@RequestBody List<@Valid Long> abilitiesIds){

        UserAbilityResponse userAbilityResponse = userAbilityService.registerUserAbilities(abilitiesIds);

        return ResponseEntity.status(HttpStatus.CREATED).body(userAbilityResponse);
    }
}
