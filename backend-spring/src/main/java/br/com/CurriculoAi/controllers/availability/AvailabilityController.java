package br.com.CurriculoAi.controllers.availability;

import br.com.CurriculoAi.dto.availability.AvailabilityRequest;
import br.com.CurriculoAi.dto.availability.AvailabilityResponse;
import br.com.CurriculoAi.services.availability.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/api/availabilities")
@RequiredArgsConstructor
@Validated
@Tag(name = "Disponibilidade Controller", description = "Responsável por  controlar as ações relionadas a disponibilidade do usuário")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @Operation(summary = "Resgistrar disponibilidade do usuário")
    @PostMapping
    public ResponseEntity<AvailabilityResponse> registerDisponibilidade(@RequestBody @Valid AvailabilityRequest availabilityRequest){

        AvailabilityResponse availabilityResponse = availabilityService.registerAvailbilityUser(availabilityRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(availabilityResponse);
    }
}
