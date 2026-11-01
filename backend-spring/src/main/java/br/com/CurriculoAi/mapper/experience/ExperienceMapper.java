package br.com.CurriculoAi.mapper.experience;

import br.com.CurriculoAi.dto.experience.ExperienceResponse;
import br.com.CurriculoAi.mapper.area.AreaMapper;
import br.com.CurriculoAi.mapper.enterprise.EnterpriseMapper;
import br.com.CurriculoAi.models.Experience;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AreaMapper.class, EnterpriseMapper.class})
public interface ExperienceMapper {

    ExperienceResponse toDto(Experience experience);
}
