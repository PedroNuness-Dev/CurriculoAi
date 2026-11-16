package br.com.CurriculoAi.services.project;

import br.com.CurriculoAi.dto.project.ProjectRequest;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.project.ProjectMapper;
import br.com.CurriculoAi.models.Ability;
import br.com.CurriculoAi.models.Project;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.project.ProjectRepository;
import br.com.CurriculoAi.services.ability.AbilityService;
import br.com.CurriculoAi.services.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService")
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private AuthService authService;

    @Mock
    private AbilityService abilityService;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    private static final Long USER_ID = 1L;
    private static final Long PROJECT_ID = 50L;

    private final Ability java = Ability.builder().abilityId(1L).name("Java").build();
    private final Ability spring = Ability.builder().abilityId(2L).name("Spring").build();

    @BeforeEach
    void setUp() {
        lenient().when(authService.getUserIdByTokenAuthenticate()).thenReturn(USER_ID);
    }

    @Test
    @DisplayName("Deve atualizar o projeto substituindo a lista de habilidades")
    void deveAtualizarProjeto_substituindoHabilidades() {
        Project project = Project.builder().projectId(PROJECT_ID).abilities(new HashSet<>(Set.of(java))).build();

        when(projectRepository.findByProjectIdAndUser_UserId(PROJECT_ID, USER_ID)).thenReturn(Optional.of(project));
        when(abilityService.findAbilitiesByIds(Set.of(2L))).thenReturn(Map.of(2L, spring));
        when(projectRepository.save(project)).thenReturn(project);

        projectService.updateProject(PROJECT_ID, new ProjectRequest("CurriculoAi", "Gerador de currículo", "https://github.com", List.of(2L)));

        assertThat(project.getTitle()).isEqualTo("CurriculoAi");
        assertThat(project.getAbilities()).containsExactly(spring);
    }

    @Test
    @DisplayName("Deve lançar 404 quando alguma habilidade do projeto não existe")
    void deveLancarNotFound_quandoHabilidadeNaoExiste() {
        Project project = Project.builder().projectId(PROJECT_ID).abilities(new HashSet<>(Set.of(java))).build();

        when(projectRepository.findByProjectIdAndUser_UserId(PROJECT_ID, USER_ID)).thenReturn(Optional.of(project));
        when(abilityService.findAbilitiesByIds(Set.of(99L))).thenReturn(Map.of());

        assertThatThrownBy(() -> projectService.updateProject(PROJECT_ID,
                new ProjectRequest("CurriculoAi", "desc", "https://github.com", List.of(99L))))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(projectRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve aceitar projeto sem habilidades (lista nula)")
    void deveAceitarProjetoSemHabilidades() {
        Project project = Project.builder().projectId(PROJECT_ID).abilities(new HashSet<>(Set.of(java))).build();

        when(projectRepository.findByProjectIdAndUser_UserId(PROJECT_ID, USER_ID)).thenReturn(Optional.of(project));
        when(abilityService.findAbilitiesByIds(Set.of())).thenReturn(Map.of());
        when(projectRepository.save(project)).thenReturn(project);

        projectService.updateProject(PROJECT_ID, new ProjectRequest("CurriculoAi", "desc", "https://github.com", null));

        assertThat(project.getAbilities()).isEmpty();
    }

    @Test
    @DisplayName("POST deve lançar 404 quando alguma habilidade não existe (antes salvava null)")
    void registerDeveLancarNotFound_quandoHabilidadeNaoExiste() {
        when(authService.getUserByTokenAuthenticate()).thenReturn(User.builder().userId(USER_ID).build());
        when(abilityService.findAbilitiesByIds(Set.of(1L, 99L))).thenReturn(Map.of(1L, java));

        assertThatThrownBy(() -> projectService.registerProjects(List.of(
                new ProjectRequest("CurriculoAi", "desc", "https://github.com", List.of(1L, 99L)))))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(projectRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Deve remover o projeto do usuário")
    void deveRemoverProjeto() {
        Project project = Project.builder().projectId(PROJECT_ID).build();

        when(projectRepository.findByProjectIdAndUser_UserId(PROJECT_ID, USER_ID)).thenReturn(Optional.of(project));

        projectService.deleteProject(PROJECT_ID);

        verify(projectRepository).delete(project);
    }

    @Test
    @DisplayName("Não deve remover projeto de outro usuário")
    void naoDeveRemover_quandoProjetoDeOutroUsuario() {
        when(projectRepository.findByProjectIdAndUser_UserId(PROJECT_ID, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.deleteProject(PROJECT_ID))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(projectRepository, never()).delete(any());
    }
}
