package br.com.CurriculoAi.controllers.availability;

import br.com.CurriculoAi.dto.availability.AvailabilityRequest;
import br.com.CurriculoAi.dto.availability.AvailabilityResponse;
import br.com.CurriculoAi.services.availability.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static br.com.CurriculoAi.config.OpenApiResponseRefs.*;

@RestController
@Tag(name = "Candidato - Disponibilidade", description = "Disponibilidade de início e modelo de trabalho do candidato autenticado (registro único)")
@RequestMapping("/api/availabilities")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('CANDIDATO')")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @Operation(
            summary = "Cadastrar disponibilidade",
            description = "Registra a data de início e o modelo de trabalho (PRESENCIAL, REMOTO ou HIBRIDO). Retorna 400 se o candidato já possuir disponibilidade cadastrada: nesse caso, use o PUT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Disponibilidade cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @PostMapping
    public ResponseEntity<AvailabilityResponse> registerDisponibilidade(@RequestBody @Valid AvailabilityRequest availabilityRequest){

        AvailabilityResponse availabilityResponse = availabilityService.registerAvailbilityUser(availabilityRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(availabilityResponse);
    }

    @Operation(
            summary = "Buscar disponibilidade",
            description = "Retorna a disponibilidade do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disponibilidade retornada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @GetMapping
    public ResponseEntity<AvailabilityResponse> getAvailabilityOfUser(){

        AvailabilityResponse availabilityResponse = availabilityService.getAvailabilityOfUser();

        return ResponseEntity.ok(availabilityResponse);
    }

    @Operation(
            summary = "Atualizar disponibilidade",
            description = "Atualiza a disponibilidade do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disponibilidade atualizada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PutMapping
    public ResponseEntity<AvailabilityResponse> updateAvailabilityOfUser(@RequestBody @Valid AvailabilityRequest availabilityRequest){

        AvailabilityResponse availabilityResponse = availabilityService.updateAvailabilityOfUser(availabilityRequest);

        return ResponseEntity.ok(availabilityResponse);
    }

    @Operation(
            summary = "Remover disponibilidade",
            description = "Remove a disponibilidade do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Disponibilidade removida com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteAvailabilityOfUser(){

        availabilityService.deleteAvailabilityOfUser();

        return ResponseEntity.noContent().build();
    }
}
