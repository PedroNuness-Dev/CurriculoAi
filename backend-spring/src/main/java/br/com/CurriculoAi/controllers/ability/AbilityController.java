package br.com.CurriculoAi.controllers.ability;

import br.com.CurriculoAi.dto.ability.AbilityRequest;
import br.com.CurriculoAi.dto.ability.AbilityResponse;
import br.com.CurriculoAi.services.ability.AbilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/abilities")
@RequiredArgsConstructor
@Tag(name = "Ability Controller", description = "Responsável por controlar as ações relacionadas às habilidades do sistema")
public class AbilityController {

    private final AbilityService abilityService;

    @Operation(summary = "Cadastrar uma habilidade")
    @PostMapping
    public ResponseEntity<AbilityResponse> createAbility(@Valid @RequestBody AbilityRequest abilityRequest) {

        AbilityResponse abilityResponse = abilityService.createAbility(abilityRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(abilityResponse);
    }

    @Operation(summary = "Listar todas as habilidades")
    @GetMapping
    public ResponseEntity<List<AbilityResponse>> findAllAbilities() {

        List<AbilityResponse> abilities = abilityService.findAllAbilities();

        return ResponseEntity.ok(abilities);
    }

    @Operation(summary = "Buscar uma habilidade pelo id")
    @GetMapping("/{abilityId}")
    public ResponseEntity<AbilityResponse> getAbilityById(@PathVariable Long abilityId) {

        AbilityResponse abilityResponse = abilityService.getAbilityById(abilityId);

        return ResponseEntity.ok(abilityResponse);
    }

    @Operation(summary = "Atualizar uma habilidade")
    @PutMapping("/{abilityId}")
    public ResponseEntity<AbilityResponse> updateAbility(@PathVariable Long abilityId,
                                                         @Valid @RequestBody AbilityRequest abilityRequest) {

        AbilityResponse abilityResponse = abilityService.updateAbility(abilityId, abilityRequest);

        return ResponseEntity.ok(abilityResponse);
    }
}