package br.com.CurriculoAi.controllers.area;

import br.com.CurriculoAi.dto.area.AreaRequest;
import br.com.CurriculoAi.dto.area.AreaResponse;
import br.com.CurriculoAi.services.area.AreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas")
@RequiredArgsConstructor
@Tag(name = "Area Controller", description = "Responsável por controlar as ações relacionadas às áreas")
public class AreaController {

    private final AreaService areaService;

    @Operation(summary = "Cadastrar uma área")
    @PostMapping("/create")
    public ResponseEntity<AreaResponse> createArea(@Valid @RequestBody AreaRequest areaRequest) {

        AreaResponse areaResponse = areaService.createArea(areaRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(areaResponse);
    }

    @Operation(summary = "Listar todas as áreas")
    @GetMapping
    public ResponseEntity<List<AreaResponse>> findAllAreas() {

        List<AreaResponse> areas = areaService.findAllAreas();

        return ResponseEntity.ok(areas);
    }

    @Operation(summary = "Buscar uma área pelo id")
    @GetMapping("/{areaId}")
    public ResponseEntity<AreaResponse> getAreaById(@PathVariable Long areaId) {

        AreaResponse areaResponse = areaService.getAreaById(areaId);

        return ResponseEntity.ok(areaResponse);
    }

    @Operation(summary = "Atualizar uma área")
    @PutMapping("/{areaId}")
    public ResponseEntity<AreaResponse> updateArea(@PathVariable Long areaId,
                                                   @Valid @RequestBody AreaRequest areaRequest) {

        AreaResponse areaResponse = areaService.updateArea(areaId, areaRequest);

        return ResponseEntity.ok(areaResponse);
    }
}