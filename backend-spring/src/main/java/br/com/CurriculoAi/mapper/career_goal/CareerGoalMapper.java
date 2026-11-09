package br.com.CurriculoAi.mapper.career_goal;

import br.com.CurriculoAi.dto.career_goal.CareerGoalResponse;
import br.com.CurriculoAi.mapper.area.AreaMapper;
import br.com.CurriculoAi.models.CareerGoal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AreaMapper.class})
public interface CareerGoalMapper {

    CareerGoalResponse toDto(CareerGoal careerGoal);
}
