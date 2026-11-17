package br.com.CurriculoAi.controllers.career_goal;

import br.com.CurriculoAi.dto.career_goal.CareerGoalRequest;
import br.com.CurriculoAi.dto.career_goal.CareerGoalResponse;
import br.com.CurriculoAi.services.career_goal.CareerGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static br.com.CurriculoAi.config.OpenApiResponseRefs.*;

@RestController
@Tag(name = "Candidato - Objetivo de Carreira", description = "Objetivo profissional do candidato autenticado (registro único)")
@RequestMapping("/api/career-goal")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CANDIDATO')")
public class CareerGoalController {

    private final CareerGoalService careerGoalService;

    @Operation(
            summary = "Cadastrar objetivo de carreira",
            description = "Registra o tipo de objetivo (ex.: PRIMEIRO_EMPREGO, ESTAGIO), a área desejada e uma descrição. Retorna 400 se o candidato já possuir objetivo cadastrado: nesse caso, use o PUT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Objetivo cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PostMapping
    public ResponseEntity<CareerGoalResponse> registerCareerGoalInUser(@RequestBody @Valid CareerGoalRequest careerGoalRequest){

        CareerGoalResponse careerGoalResponse = careerGoalService.registerCareerGoalInUser(careerGoalRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(careerGoalResponse);
    }

    @Operation(
            summary = "Buscar objetivo de carreira",
            description = "Retorna o objetivo de carreira do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Objetivo retornado com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @GetMapping
    public ResponseEntity<CareerGoalResponse> getCareerGoalOfUser(){

        CareerGoalResponse careerGoalResponse = careerGoalService.getCareerGoalOfUser();

        return ResponseEntity.ok(careerGoalResponse);
    }

    @Operation(
            summary = "Atualizar objetivo de carreira",
            description = "Atualiza o objetivo de carreira do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Objetivo atualizado com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PutMapping
    public ResponseEntity<CareerGoalResponse> updateCareerGoalOfUser(@RequestBody @Valid CareerGoalRequest careerGoalRequest){

        CareerGoalResponse careerGoalResponse = careerGoalService.updateCareerGoalOfUser(careerGoalRequest);

        return ResponseEntity.ok(careerGoalResponse);
    }

    @Operation(
            summary = "Remover objetivo de carreira",
            description = "Remove o objetivo de carreira do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Objetivo removido com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteCareerGoalOfUser(){

        careerGoalService.deleteCareerGoalOfUser();

        return ResponseEntity.noContent().build();
    }
}
