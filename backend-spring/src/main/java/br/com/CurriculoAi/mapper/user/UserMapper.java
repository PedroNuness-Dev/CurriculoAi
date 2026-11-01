package br.com.CurriculoAi.mapper.user;

import br.com.CurriculoAi.dto.user.UserFullContent;
import br.com.CurriculoAi.dto.user.UserResponse;
import br.com.CurriculoAi.mapper.ability.AbilityMapper;
import br.com.CurriculoAi.mapper.area.AreaMapper;
import br.com.CurriculoAi.mapper.availability.AvailabilityMapper;
import br.com.CurriculoAi.mapper.certification.CertificationMapper;
import br.com.CurriculoAi.mapper.experience.ExperienceMapper;
import br.com.CurriculoAi.mapper.formation.FormationMapper;
import br.com.CurriculoAi.mapper.project.ProjectMapper;
import br.com.CurriculoAi.mapper.social_network.SocialNetworkMapper;
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
        ProjectMapper.class,
        SocialNetworkMapper.class,
        AbilityMapper.class
})
public interface UserMapper{

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    UserResponse toDto(User user);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "area", source = "area")
    @Mapping(target = "formations", source = "formations")
    @Mapping(target = "experiences", source = "experiences")
    @Mapping(target = "certifications", source = "certifications")
    @Mapping(target = "availability", source = "availability")
    @Mapping(target = "languages", source = "languages")
    @Mapping(target = "projects", source = "projects")
    @Mapping(target = "socialNetworks", source = "socialNetworks")
    @Mapping(target = "abilities", source = "abilities")
    UserFullContent toFullContentDto(User user);
}