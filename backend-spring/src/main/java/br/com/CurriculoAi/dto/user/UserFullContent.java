package br.com.CurriculoAi.dto.user;

import br.com.CurriculoAi.dto.ability.AbilityResponse;
import br.com.CurriculoAi.dto.area.AreaResponse;
import br.com.CurriculoAi.dto.availability.AvailabilityResponse;
import br.com.CurriculoAi.dto.certification.CertificationResponse;
import br.com.CurriculoAi.dto.experience.ExperienceResponse;
import br.com.CurriculoAi.dto.formation.FormationResponse;
import br.com.CurriculoAi.dto.project.ProjectResponse;

import java.util.List;

public record UserFullContent(
        UserResponse user,
        AreaResponse area,
        AvailabilityResponse availability,
        List<FormationResponse> formations,
        List<ExperienceResponse> experiences,
        List<CertificationResponse> certifications,
        List<UserLanguageResponse> languages,
        List<ProjectResponse> projects,
        List<UserSocialNetworkResponse> socialNetworks,
        List<AbilityResponse> abilities
) {
}
