package br.com.CurriculoAi.services.project;

import br.com.CurriculoAi.dto.project.ProjectRequest;
import br.com.CurriculoAi.dto.project.ProjectResponse;
import br.com.CurriculoAi.mapper.project.ProjectMapper;
import br.com.CurriculoAi.models.Ability;
import br.com.CurriculoAi.models.Project;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.project.ProjectRepository;
import br.com.CurriculoAi.services.ability.AbilityService;
import br.com.CurriculoAi.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final AuthService authService;
    private final AbilityService abilityService;
    private final ProjectMapper projectMapper;

    @Transactional
    public List<ProjectResponse> registerProjects(List<ProjectRequest> projectRequests){

        if (projectRequests.isEmpty()) throw new IllegalArgumentException("Lista de projetos está vaiza");

        User user = authService.getUserByTokenAuthenticate();

        log.info("Inicinado cadastro de projetos do usuário [{}], com um total de [{}]", user.getEmail(), projectRequests.size());

        Map<Long, Ability> abilitiesMap = getAbilitiesByIds(projectRequests);

        List<Project> projectsForSave = projectRequests
                .stream()
                .map(projectRequest -> {

                    Set<Ability> abilities = projectRequest.abilitiesIds()
                            .stream()
                            .map(abilitiesMap::get)
                            .collect(Collectors.toSet());

                    return Project.builder()
                            .title(projectRequest.title())
                            .description(projectRequest.description())
                            .link(projectRequest.link())
                            .user(user)
                            .abilities(abilities)
                            .build();
                })
                .toList();

        List<Project> projectsSaved = projectRepository.saveAll(projectsForSave);

        return projectsSaved
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    private Map<Long, Ability> getAbilitiesByIds(List<ProjectRequest> projectRequests){

        Set<Long> abilitiesIds = new HashSet<>();

        for (ProjectRequest projectRequest : projectRequests){
            abilitiesIds.addAll(projectRequest.abilitiesIds());
        }

        return abilityService.findAbilitiesByIds(abilitiesIds);
    }
}
