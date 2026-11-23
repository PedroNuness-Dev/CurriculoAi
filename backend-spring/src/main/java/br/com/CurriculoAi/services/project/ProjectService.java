package br.com.CurriculoAi.services.project;

import java.util.Optional;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
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

                    Set<Ability> abilities = resolveAbilities(projectRequest, abilitiesMap);

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
            if (projectRequest.abilitiesIds() != null) abilitiesIds.addAll(projectRequest.abilitiesIds());
        }

        return abilityService.findAbilitiesByIds(abilitiesIds);
    }


    public List<ProjectResponse> findAllProjectsOfUser(){

        Long userId = authService.getUserIdByTokenAuthenticate();

        return projectRepository.findAllByUser_UserIdOrderByProjectIdAsc(userId)
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    public ProjectResponse getProjectById(Long projectId){

        Project project = findProjectOfAuthenticatedUser(projectId);

        return projectMapper.toDto(project);
    }

    // Substitui os dados e a lista inteira de habilidades do projeto
    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectRequest projectRequest){

        log.info("Iniciando atualização do projeto com id [{}]", projectId);

        Project project = findProjectOfAuthenticatedUser(projectId);

        Map<Long, Ability> abilitiesMap = getAbilitiesByIds(List.of(projectRequest));

        project.setTitle(projectRequest.title());
        project.setDescription(projectRequest.description());
        project.setLink(projectRequest.link());
        project.getAbilities().clear();
        project.getAbilities().addAll(resolveAbilities(projectRequest, abilitiesMap));

        Project updatedProject = projectRepository.save(project);

        log.info("Projeto com id [{}] atualizado com sucesso", projectId);

        return projectMapper.toDto(updatedProject);
    }

    @Transactional
    public void deleteProject(Long projectId){

        log.info("Iniciando remoção do projeto com id [{}]", projectId);

        Project project = findProjectOfAuthenticatedUser(projectId);

        projectRepository.delete(project);

        log.info("Projeto com id [{}] removido com sucesso", projectId);
    }

    // Busca já filtrando pelo usuário autenticado: se o projeto for de outro usuário, retorna 404
    private Project findProjectOfAuthenticatedUser(Long projectId){

        Long userId = authService.getUserIdByTokenAuthenticate();

        return projectRepository.findByProjectIdAndUser_UserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto com id: "+projectId+" não encontrado"));
    }

    // Converte os ids em habilidades, lançando 404 se algum id não existir
    private Set<Ability> resolveAbilities(ProjectRequest projectRequest, Map<Long, Ability> abilitiesMap){

        if (projectRequest.abilitiesIds() == null) return new HashSet<>();

        return projectRequest.abilitiesIds()
                .stream()
                .map(abilityId -> Optional.ofNullable(abilitiesMap.get(abilityId))
                        .orElseThrow(() -> new ResourceNotFoundException("Habilidade com id: "+abilityId+" não encontrada")))
                .collect(Collectors.toSet());
    }
}
