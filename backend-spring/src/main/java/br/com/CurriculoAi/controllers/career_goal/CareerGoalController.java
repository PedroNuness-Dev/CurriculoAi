package br.com.CurriculoAi.controllers.career_goal;

import br.com.CurriculoAi.dto.career_goal.CareerGoalRequest;
import br.com.CurriculoAi.dto.career_goal.CareerGoalResponse;
import br.com.CurriculoAi.services.career_goal.CareerGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/career-goal")
@RequiredArgsConstructor
public class CareerGoalController {

    private final CareerGoalService careerGoalService;

    @PostMapping
    public ResponseEntity<CareerGoalResponse> registerCareerGoalInUser(@RequestBody @Valid CareerGoalRequest careerGoalRequest){

        CareerGoalResponse careerGoalResponse = careerGoalService.registerCareerGoalInUser(careerGoalRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(careerGoalResponse);
    }
}
