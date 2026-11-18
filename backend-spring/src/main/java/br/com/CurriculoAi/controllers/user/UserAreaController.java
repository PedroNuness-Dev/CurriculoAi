package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserAreaResponse;
import br.com.CurriculoAi.services.user.UserAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static br.com.CurriculoAi.config.OpenApiResponseRefs.*;

@RestController
@Tag(name = "Candidato - Área", description = "Área de atuação do candidato autenticado (vínculo único com o catálogo de áreas)")
@RequestMapping("/api/users/area")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CANDIDATO')")
public class UserAreaController {

    private final UserAreaService userAreaService;

    @Operation(
            summary = "Vincular área",
            description = "Vincula uma área do catálogo ao candidato autenticado, substituindo a atual se houver."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Área vinculada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PostMapping("/{areaId}")
    public ResponseEntity<UserAreaResponse> addAreaToUser(@Parameter(description = "Id da área no catálogo") @PathVariable Long areaId){

        UserAreaResponse userAreaResponse = userAreaService.addAreaToUser(areaId);

        return ResponseEntity.ok(userAreaResponse);
    }

    @Operation(
            summary = "Buscar área",
            description = "Retorna a área vinculada ao candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Área retornada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @GetMapping
    public ResponseEntity<UserAreaResponse> getAreaOfUser(){

        UserAreaResponse userAreaResponse = userAreaService.getAreaOfUser();

        return ResponseEntity.ok(userAreaResponse);
    }

    @Operation(
            summary = "Trocar área",
            description = "Substitui a área vinculada ao candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Área atualizada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PutMapping("/{areaId}")
    public ResponseEntity<UserAreaResponse> updateAreaOfUser(@Parameter(description = "Id da área no catálogo") @PathVariable Long areaId){

        UserAreaResponse userAreaResponse = userAreaService.updateAreaOfUser(areaId);

        return ResponseEntity.ok(userAreaResponse);
    }

    @Operation(
            summary = "Remover área",
            description = "Remove o vínculo da área com o candidato. A área continua no catálogo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Área desvinculada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @DeleteMapping
    public ResponseEntity<Void> removeAreaOfUser(){

        userAreaService.removeAreaOfUser();

        return ResponseEntity.noContent().build();
    }
}
