package br.com.CurriculoAi.controllers.formation;

import br.com.CurriculoAi.dto.formation.FormationRequest;
import br.com.CurriculoAi.dto.formation.FormationResponse;
import br.com.CurriculoAi.services.formation.FormationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Formation Controller", description = "Responsável por controlar as ações relacionadas às formações do usuário")
public class FormationController {

    private final FormationService formationService;

    @Operation(summary = "Cadastrar formações do usuário autenticado")
    @PostMapping
    public ResponseEntity<List<FormationResponse>> registerFormationsByUser(
            @RequestBody List<@Valid FormationRequest> formationRequests) {

        List<FormationResponse> formationResponses = formationService.registerFormationsByUser(formationRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(formationResponses);
    }

    @Operation(summary = "Listar todas as formações do usuário autenticado")
    @GetMapping
    public ResponseEntity<List<FormationResponse>> findAllFormationsUser() {

        List<FormationResponse> formations = formationService.findAllFormationsUser();

        return ResponseEntity.ok(formations);
    }

    @Operation(summary = "Buscar uma formação do usuário autenticado pelo id")
    @GetMapping("/{formationId}")
    public ResponseEntity<FormationResponse> getFormationUserByFormationId(@PathVariable Long formationId) {

        FormationResponse formationResponse = formationService.getFormationUserByFormationId(formationId);

        return ResponseEntity.ok(formationResponse);
    }
}