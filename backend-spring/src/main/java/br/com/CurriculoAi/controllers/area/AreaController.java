package br.com.CurriculoAi.controllers.area;

import br.com.CurriculoAi.dto.area.AreaRequest;
import br.com.CurriculoAi.dto.area.AreaResponse;
import br.com.CurriculoAi.services.area.AreaService;
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
@Tag(name = "Catálogo - Áreas", description = "Áreas de atuação profissional (ex.: Backend, Dados) usadas no perfil, experiências e objetivo de carreira. Leitura liberada para usuários autenticados; escrita restrita ao perfil ADMIN.")
@RequestMapping("/api/areas")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @Operation(
            summary = "Cadastrar uma área",
            description = "Cadastra uma nova área no catálogo da aplicação. Requer o perfil ADMIN. Retorna 400 se já existir uma área com o mesmo nome."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Área cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<AreaResponse> createArea(@Valid @RequestBody AreaRequest areaRequest) {

        AreaResponse areaResponse = areaService.createArea(areaRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(areaResponse);
    }

    @Operation(
            summary = "Listar áreas",
            description = "Retorna todas as áreas cadastradas no catálogo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de áreas retornada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<AreaResponse>> findAllAreas() {

        List<AreaResponse> areas = areaService.findAllAreas();

        return ResponseEntity.ok(areas);
    }

    @Operation(
            summary = "Buscar área por id",
            description = "Retorna a área correspondente ao id informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Área encontrada"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{areaId}")
    public ResponseEntity<AreaResponse> getAreaById(@Parameter(description = "Id da área") @PathVariable Long areaId) {

        AreaResponse areaResponse = areaService.getAreaById(areaId);

        return ResponseEntity.ok(areaResponse);
    }

    @Operation(
            summary = "Atualizar uma área",
            description = "Atualiza os dados de uma área do catálogo. Requer o perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Área atualizada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{areaId}")
    public ResponseEntity<AreaResponse> updateArea(@Parameter(description = "Id da área") @PathVariable Long areaId,
                                                   @Valid @RequestBody AreaRequest areaRequest) {

        AreaResponse areaResponse = areaService.updateArea(areaId, areaRequest);

        return ResponseEntity.ok(areaResponse);
    }
}