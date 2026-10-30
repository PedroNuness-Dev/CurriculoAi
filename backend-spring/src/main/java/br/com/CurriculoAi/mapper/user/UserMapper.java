package br.com.CurriculoAi.mapper.user;

import br.com.CurriculoAi.dto.user.UserResponse;
import br.com.CurriculoAi.dto.response.UsuarioFullContentDtoResponse;
import br.com.CurriculoAi.mapper.area.AreaMapper;
import br.com.CurriculoAi.mapper.availability.AvailabilityMapper;
import br.com.CurriculoAi.mapper.certification.CertificationMapper;
import br.com.CurriculoAi.mapper.experience.ExperienceMapper;
import br.com.CurriculoAi.mapper.formation.FormationMapper;
import br.com.CurriculoAi.mapper.project.ProjectMapper;
import br.com.CurriculoAi.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        FormationMapper.class,
        ExperienceMapper.class,
        CertificationMapper.class,
        AvailabilityMapper.class,
        UserLanguageMapper.class,
        AreaMapper.class,
        ProjectMapper.class
})
public interface UserMapper{

    @Mapping(target = "id", source = "id")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "email", source = "email")
    UserResponse toDto(User user);

    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "area", source = "area")
    @Mapping(target = "formacoes", source = "formacoes")
    @Mapping(target = "experiencias", source = "experiencias")
    @Mapping(target = "certificoes", source = "certificacoes")
    @Mapping(target = "disponibilidade", source = "disponibilidade")
    @Mapping(target = "idiomas", source = "idiomas")
    @Mapping(target = "projetos", source = "projetos")
    UsuarioFullContentDtoResponse toFullContentDto(User user);
}