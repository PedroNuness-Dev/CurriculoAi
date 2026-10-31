package br.com.CurriculoAi.controllers.institution;

import br.com.CurriculoAi.dto.institution.InstitutionRequest;
import br.com.CurriculoAi.dto.institution.InstitutionResponse;
import br.com.CurriculoAi.services.institution.InstitutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/institutions")
@Tag(name = "Instituição Controller", description = "Responsável pelas ações relacionadas as instituições do sistema")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;

    @Operation(summary = "Cadastrar uma instituição")
    @PostMapping
    public ResponseEntity<InstitutionResponse> createInstitution(
            @Valid @RequestBody InstitutionRequest institutionRequest) {

        InstitutionResponse institutionResponse = institutionService.createInstitution(institutionRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(institutionResponse);
    }

    @Operation(summary = "Listar todas as instituições")
    @GetMapping
    public ResponseEntity<List<InstitutionResponse>> findAllInstitutions() {

        List<InstitutionResponse> institutions = institutionService.findAllInstitutions();

        return ResponseEntity.ok(institutions);
    }

    @Operation(summary = "Buscar uma instituição pelo id")
    @GetMapping("/{institutionId}")
    public ResponseEntity<InstitutionResponse> getInstitutionById(@PathVariable Long institutionId) {

        InstitutionResponse institutionResponse = institutionService.getInstitutionById(institutionId);

        return ResponseEntity.ok(institutionResponse);
    }

    @Operation(summary = "Atualizar uma instituição")
    @PutMapping("/{institutionId}")
    public ResponseEntity<InstitutionResponse> updateInstitution(@PathVariable Long institutionId,
                                                                 @Valid @RequestBody InstitutionRequest institutionRequest) {

        InstitutionResponse institutionResponse = institutionService.updateInstitution(institutionId, institutionRequest);

        return ResponseEntity.ok(institutionResponse);
    }
}