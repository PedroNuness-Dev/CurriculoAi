package br.com.CurriculoAi.services.career_goal;

import br.com.CurriculoAi.dto.career_goal.CareerGoalRequest;
import br.com.CurriculoAi.enums.careergoal.GoalType;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.career_goal.CareerGoalMapper;
import br.com.CurriculoAi.models.Area;
import br.com.CurriculoAi.models.CareerGoal;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.career_goal.CareerGoalRepository;
import br.com.CurriculoAi.services.area.AreaService;
import br.com.CurriculoAi.services.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CareerGoalService")
class CareerGoalServiceTest {

    @Mock
    private CareerGoalRepository careerGoalRepository;

    @Mock
    private AuthService authService;

    @Mock
    private AreaService areaService;

    @Mock
    private CareerGoalMapper careerGoalMapper;

    @InjectMocks
    private CareerGoalService careerGoalService;

    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        lenient().when(authService.getUserIdByTokenAuthenticate()).thenReturn(USER_ID);
    }

    @Test
    @DisplayName("POST deve recusar quando o usuário já possui objetivo de carreira")
    void naoDeveCadastrar_quandoJaPossuiObjetivo() {
        when(authService.getUserByTokenAuthenticate()).thenReturn(User.builder().userId(USER_ID).build());
        when(careerGoalRepository.existsByUser_UserId(USER_ID)).thenReturn(true);

        assertThatThrownBy(() -> careerGoalService.registerCareerGoalInUser(
                new CareerGoalRequest("ESTAGIO", 2L, "Quero estagiar com backend")))
                .isInstanceOf(IllegalArgumentException.class);

        verify(careerGoalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar 404 ao buscar quando o usuário não possui objetivo")
    void deveLancarNotFound_quandoSemObjetivo() {
        when(careerGoalRepository.findByUser_UserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> careerGoalService.getCareerGoalOfUser())
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar o objetivo de carreira do usuário")
    void deveAtualizarObjetivo() {
        CareerGoal careerGoal = CareerGoal.builder().careerGoalId(8L).goalType(GoalType.ESTAGIO).build();
        Area area = Area.builder().areaId(3L).build();

        when(careerGoalRepository.findByUser_UserId(USER_ID)).thenReturn(Optional.of(careerGoal));
        when(areaService.findAreaById(3L)).thenReturn(area);
        when(careerGoalRepository.saveAndFlush(careerGoal)).thenReturn(careerGoal);

        careerGoalService.updateCareerGoalOfUser(new CareerGoalRequest("PROGRESSAO_CARREIRA", 3L, "Chegar a pleno"));

        assertThat(careerGoal.getGoalType()).isEqualTo(GoalType.PROGRESSAO_CARREIRA);
        assertThat(careerGoal.getArea()).isEqualTo(area);
        assertThat(careerGoal.getDescription()).isEqualTo("Chegar a pleno");
    }

    @Test
    @DisplayName("Deve remover o objetivo de carreira do usuário")
    void deveRemoverObjetivo() {
        CareerGoal careerGoal = CareerGoal.builder().careerGoalId(8L).build();

        when(careerGoalRepository.findByUser_UserId(USER_ID)).thenReturn(Optional.of(careerGoal));

        careerGoalService.deleteCareerGoalOfUser();

        verify(careerGoalRepository).delete(careerGoal);
    }
}
