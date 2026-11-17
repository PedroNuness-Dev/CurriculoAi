package br.com.CurriculoAi.services.formation;

import br.com.CurriculoAi.dto.formation.FormationRequest;
import br.com.CurriculoAi.enums.formation.FormationType;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.formation.FormationMapper;
import br.com.CurriculoAi.models.Course;
import br.com.CurriculoAi.models.Formation;
import br.com.CurriculoAi.repositories.formation.FormationRepository;
import br.com.CurriculoAi.services.auth.AuthService;
import br.com.CurriculoAi.services.course.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FormationService")
class FormationServiceTest {

    @Mock
    private CourseService courseService;

    @Mock
    private FormationRepository formationRepository;

    @Mock
    private AuthService authService;

    @Mock
    private FormationMapper formationMapper;

    @InjectMocks
    private FormationService formationService;

    private static final Long USER_ID = 1L;
    private static final Long FORMATION_ID = 10L;

    @BeforeEach
    void setUp() {
        lenient().when(authService.getUserIdByTokenAuthenticate()).thenReturn(USER_ID);
    }

    @Test
    @DisplayName("Deve atualizar a formação e marcar como em andamento quando não há data de término")
    void deveAtualizarFormacao_emAndamento() {
        Formation formation = Formation.builder().formationId(FORMATION_ID).inProgress(false).build();
        Course course = Course.builder().courseId(5L).build();

        when(formationRepository.findByFormationIdAndUser_UserId(FORMATION_ID, USER_ID)).thenReturn(Optional.of(formation));
        when(courseService.findCourseById(5L)).thenReturn(course);
        when(formationRepository.save(formation)).thenReturn(formation);

        formationService.updateFormation(FORMATION_ID,
                new FormationRequest(5L, "bacharelado", LocalDate.of(2024, 1, 1), null));

        assertThat(formation.getCourse()).isEqualTo(course);
        assertThat(formation.getFormationType()).isEqualTo(FormationType.BACHARELADO);
        assertThat(formation.getInProgress()).isTrue();
    }

    @Test
    @DisplayName("Deve marcar como concluída quando a data de término é informada")
    void deveAtualizarFormacao_concluida() {
        Formation formation = Formation.builder().formationId(FORMATION_ID).inProgress(true).build();

        when(formationRepository.findByFormationIdAndUser_UserId(FORMATION_ID, USER_ID)).thenReturn(Optional.of(formation));
        when(courseService.findCourseById(5L)).thenReturn(Course.builder().courseId(5L).build());
        when(formationRepository.save(formation)).thenReturn(formation);

        formationService.updateFormation(FORMATION_ID,
                new FormationRequest(5L, "bacharelado", LocalDate.of(2020, 1, 1), LocalDate.of(2024, 1, 1)));

        assertThat(formation.getInProgress()).isFalse();
    }

    @Test
    @DisplayName("Não deve atualizar formação de outro usuário")
    void naoDeveAtualizar_quandoFormacaoDeOutroUsuario() {
        when(formationRepository.findByFormationIdAndUser_UserId(FORMATION_ID, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formationService.updateFormation(FORMATION_ID,
                new FormationRequest(5L, "bacharelado", LocalDate.now(), null)))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(formationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve remover a formação do usuário")
    void deveRemoverFormacao() {
        Formation formation = Formation.builder().formationId(FORMATION_ID).build();

        when(formationRepository.findByFormationIdAndUser_UserId(FORMATION_ID, USER_ID)).thenReturn(Optional.of(formation));

        formationService.deleteFormation(FORMATION_ID);

        verify(formationRepository).delete(formation);
    }

    @Test
    @DisplayName("Não deve remover formação de outro usuário")
    void naoDeveRemover_quandoFormacaoDeOutroUsuario() {
        when(formationRepository.findByFormationIdAndUser_UserId(FORMATION_ID, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formationService.deleteFormation(FORMATION_ID))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(formationRepository, never()).delete(any());
    }
}
