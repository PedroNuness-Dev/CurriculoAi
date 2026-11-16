package br.com.CurriculoAi.controllers.career_goal;

import br.com.CurriculoAi.dto.career_goal.CareerGoalRequest;
import br.com.CurriculoAi.dto.career_goal.CareerGoalResponse;
import br.com.CurriculoAi.services.career_goal.CareerGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/career-goal")
@Tag(name = "Career Goal Controller", description = "Responsável pelas ações relacionadas ao objetivo de carreira do usuário")
@RequiredArgsConstructor
public class CareerGoalController {

    private final CareerGoalService careerGoalService;

    @Operation(summary = "Cadastrar o objetivo de carreira do usuário autenticado")
    @PostMapping
    public ResponseEntity<CareerGoalResponse> registerCareerGoalInUser(@RequestBody @Valid CareerGoalRequest careerGoalRequest){

        CareerGoalResponse careerGoalResponse = careerGoalService.registerCareerGoalInUser(careerGoalRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(careerGoalResponse);
    }

    @Operation(summary = "Buscar o objetivo de carreira do usuário autenticado")
    @GetMapping
    public ResponseEntity<CareerGoalResponse> getCareerGoalOfUser(){

        CareerGoalResponse careerGoalResponse = careerGoalService.getCareerGoalOfUser();

        return ResponseEntity.ok(careerGoalResponse);
    }

    @Operation(summary = "Atualizar o objetivo de carreira do usuário autenticado")
    @PutMapping
    public ResponseEntity<CareerGoalResponse> updateCareerGoalOfUser(@RequestBody @Valid CareerGoalRequest careerGoalRequest){

        CareerGoalResponse careerGoalResponse = careerGoalService.updateCareerGoalOfUser(careerGoalRequest);

        return ResponseEntity.ok(careerGoalResponse);
    }

    @Operation(summary = "Remover o objetivo de carreira do usuário autenticado")
    @DeleteMapping
    public ResponseEntity<Void> deleteCareerGoalOfUser(){

        careerGoalService.deleteCareerGoalOfUser();

        return ResponseEntity.noContent().build();
    }
}
