package br.com.CurriculoAi.controllers.enterprise;

import br.com.CurriculoAi.dto.enterprise.EnterpriseRequest;
import br.com.CurriculoAi.dto.enterprise.EnterpriseResponse;
import br.com.CurriculoAi.services.enterprise.EnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enterprises")
@Tag(name = "Empresa Controller", description = "Responsável pelas ações relacionadas as Empresas do sistema")
@RequiredArgsConstructor
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    @Operation(summary = "Cadastrar uma empresa")
    @PostMapping
    public ResponseEntity<EnterpriseResponse> createEnterprise(
            @Valid @RequestBody EnterpriseRequest enterpriseRequest) {

        EnterpriseResponse enterpriseResponse = enterpriseService.createEnterprise(enterpriseRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(enterpriseResponse);
    }

    @Operation(summary = "Listar todas as empresas")
    @GetMapping
    public ResponseEntity<List<EnterpriseResponse>> findAllEnterprises() {

        List<EnterpriseResponse> enterprises = enterpriseService.findAllEnterprises();

        return ResponseEntity.ok(enterprises);
    }

    @Operation(summary = "Buscar uma empresa pelo id")
    @GetMapping("/{enterpriseId}")
    public ResponseEntity<EnterpriseResponse> getEnterpriseById(@PathVariable Long enterpriseId) {

        EnterpriseResponse enterpriseResponse = enterpriseService.getEnterpriseById(enterpriseId);

        return ResponseEntity.ok(enterpriseResponse);
    }

    @Operation(summary = "Atualizar uma empresa")
    @PutMapping("/{enterpriseId}")
    public ResponseEntity<EnterpriseResponse> updateEnterprise(@PathVariable Long enterpriseId,
                                                               @Valid @RequestBody EnterpriseRequest enterpriseRequest) {

        EnterpriseResponse enterpriseResponse = enterpriseService.updateEnterprise(enterpriseId, enterpriseRequest);

        return ResponseEntity.ok(enterpriseResponse);
    }
}