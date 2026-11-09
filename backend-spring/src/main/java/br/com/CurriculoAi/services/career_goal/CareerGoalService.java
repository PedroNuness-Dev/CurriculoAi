package br.com.CurriculoAi.services.career_goal;

import br.com.CurriculoAi.dto.career_goal.CareerGoalRequest;
import br.com.CurriculoAi.dto.career_goal.CareerGoalResponse;
import br.com.CurriculoAi.enums.careergoal.GoalType;
import br.com.CurriculoAi.mapper.career_goal.CareerGoalMapper;
import br.com.CurriculoAi.models.Area;
import br.com.CurriculoAi.models.CareerGoal;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.career_goal.CareerGoalRepository;
import br.com.CurriculoAi.services.area.AreaService;
import br.com.CurriculoAi.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CareerGoalService {

    private final CareerGoalRepository careerGoalRepository;
    private final AuthService authService;
    private final AreaService areaService;
    private final CareerGoalMapper careerGoalMapper;

    @Transactional
    public CareerGoalResponse registerCareerGoalInUser(CareerGoalRequest careerGoalRequest){

        User user = authService.getUserByTokenAuthenticate();

        Area area = areaService.findAreaById(careerGoalRequest.areaId());

        log.info("Registrando objetivo do usuário {} como {}", user.getUserId(), careerGoalRequest.goalType());

        GoalType goalType = GoalType.from(careerGoalRequest.goalType());

        CareerGoal careerGoalForSave = CareerGoal.builder()
                .goalType(goalType)
                .description(careerGoalRequest.description())
                .area(area)
                .user(user)
                .build();

        CareerGoal careerGoalSaved = careerGoalRepository.save(careerGoalForSave);

        return careerGoalMapper.toDto(careerGoalSaved);
    }
}
