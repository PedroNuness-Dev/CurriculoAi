package br.com.CurriculoAi.controllers.enterprise;

import br.com.CurriculoAi.dto.enterprise.EnterpriseRequest;
import br.com.CurriculoAi.dto.enterprise.EnterpriseResponse;
import br.com.CurriculoAi.services.enterprise.EnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static br.com.CurriculoAi.config.OpenApiResponseRefs.*;

@RestController
@Tag(name = "Catálogo - Empresas", description = "Empresas disponíveis para vincular às experiências profissionais. Leitura liberada para usuários autenticados; cadastro permitido a candidatos e administradores; edição restrita ao perfil ADMIN.")
@RequestMapping("/api/enterprises")
@RequiredArgsConstructor
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    @Operation(
            summary = "Cadastrar uma empresa",
            description = "Cadastra uma nova empresa no catálogo da aplicação. Requer autenticação como ADMIN ou CANDIDATO. Retorna 400 se já existir uma empresa com o mesmo nome."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Empresa cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO')")
    @PostMapping
    public ResponseEntity<EnterpriseResponse> createEnterprise(
            @Valid @RequestBody EnterpriseRequest enterpriseRequest) {

        EnterpriseResponse enterpriseResponse = enterpriseService.createEnterprise(enterpriseRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(enterpriseResponse);
    }

    @Operation(
            summary = "Listar empresas",
            description = "Retorna todas as empresas cadastradas no catálogo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de empresas retornada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<EnterpriseResponse>> findAllEnterprises() {

        List<EnterpriseResponse> enterprises = enterpriseService.findAllEnterprises();

        return ResponseEntity.ok(enterprises);
    }

    @Operation(
            summary = "Buscar empresa por id",
            description = "Retorna a empresa correspondente ao id informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empresa encontrada"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{enterpriseId}")
    public ResponseEntity<EnterpriseResponse> getEnterpriseById(@Parameter(description = "Id da empresa") @PathVariable Long enterpriseId) {

        EnterpriseResponse enterpriseResponse = enterpriseService.getEnterpriseById(enterpriseId);

        return ResponseEntity.ok(enterpriseResponse);
    }

    @Operation(
            summary = "Atualizar uma empresa",
            description = "Atualiza os dados de uma empresa do catálogo. Requer o perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empresa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{enterpriseId}")
    public ResponseEntity<EnterpriseResponse> updateEnterprise(@Parameter(description = "Id da empresa") @PathVariable Long enterpriseId,
                                                               @Valid @RequestBody EnterpriseRequest enterpriseRequest) {

        EnterpriseResponse enterpriseResponse = enterpriseService.updateEnterprise(enterpriseId, enterpriseRequest);

        return ResponseEntity.ok(enterpriseResponse);
    }
}