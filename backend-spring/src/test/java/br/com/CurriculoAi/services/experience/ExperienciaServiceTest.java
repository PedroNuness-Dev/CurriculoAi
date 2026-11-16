package br.com.CurriculoAi.services.experience;

import br.com.CurriculoAi.dto.experience.ExperienceRequest;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.experience.ExperienceMapper;
import br.com.CurriculoAi.models.Area;
import br.com.CurriculoAi.models.Enterprise;
import br.com.CurriculoAi.models.Experience;
import br.com.CurriculoAi.repositories.experience.ExperienceRepository;
import br.com.CurriculoAi.services.area.AreaService;
import br.com.CurriculoAi.services.auth.AuthService;
import br.com.CurriculoAi.services.enterprise.EnterpriseService;
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
@DisplayName("ExperienciaService")
class ExperienciaServiceTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private AreaService areaService;

    @Mock
    private EnterpriseService enterpriseService;

    @Mock
    private ExperienceMapper experienceMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ExperienciaService experienciaService;

    private static final Long USER_ID = 1L;
    private static final Long EXPERIENCE_ID = 40L;

    @BeforeEach
    void setUp() {
        lenient().when(authService.getUserIdByTokenAuthenticate()).thenReturn(USER_ID);
    }

    private ExperienceRequest request(LocalDate finishDate) {
        return new ExperienceRequest("Dev Backend", "APIs REST com Spring", 2L, 3L,
                LocalDate.of(2023, 1, 1), "Júnior", finishDate, null);
    }

    @Test
    @DisplayName("Deve atualizar a experiência e marcar como trabalho atual quando não há data de término")
    void deveAtualizarExperiencia_trabalhoAtual() {
        Experience experience = Experience.builder().experienceId(EXPERIENCE_ID).workCurrent(false).build();
        Area area = Area.builder().areaId(2L).build();
        Enterprise enterprise = Enterprise.builder().enterpriseId(3L).build();

        when(experienceRepository.findByExperienceIdAndUser_UserId(EXPERIENCE_ID, USER_ID)).thenReturn(Optional.of(experience));
        when(areaService.findAreaById(2L)).thenReturn(area);
        when(enterpriseService.findEnterpriseById(3L)).thenReturn(enterprise);
        when(experienceRepository.save(experience)).thenReturn(experience);

        experienciaService.updateExperience(EXPERIENCE_ID, request(null));

        assertThat(experience.getArea()).isEqualTo(area);
        assertThat(experience.getEnterprise()).isEqualTo(enterprise);
        assertThat(experience.getTitle()).isEqualTo("Dev Backend");
        assertThat(experience.getWorkCurrent()).isTrue();
    }

    @Test
    @DisplayName("Deve marcar como trabalho encerrado quando a data de término é informada")
    void deveAtualizarExperiencia_encerrada() {
        Experience experience = Experience.builder().experienceId(EXPERIENCE_ID).workCurrent(true).build();

        when(experienceRepository.findByExperienceIdAndUser_UserId(EXPERIENCE_ID, USER_ID)).thenReturn(Optional.of(experience));
        when(areaService.findAreaById(2L)).thenReturn(Area.builder().areaId(2L).build());
        when(enterpriseService.findEnterpriseById(3L)).thenReturn(Enterprise.builder().enterpriseId(3L).build());
        when(experienceRepository.save(experience)).thenReturn(experience);

        experienciaService.updateExperience(EXPERIENCE_ID, request(LocalDate.of(2024, 6, 1)));

        assertThat(experience.getWorkCurrent()).isFalse();
    }

    @Test
    @DisplayName("Não deve atualizar experiência de outro usuário")
    void naoDeveAtualizar_quandoExperienciaDeOutroUsuario() {
        when(experienceRepository.findByExperienceIdAndUser_UserId(EXPERIENCE_ID, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> experienciaService.updateExperience(EXPERIENCE_ID, request(null)))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(experienceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve remover a experiência do usuário")
    void deveRemoverExperiencia() {
        Experience experience = Experience.builder().experienceId(EXPERIENCE_ID).build();

        when(experienceRepository.findByExperienceIdAndUser_UserId(EXPERIENCE_ID, USER_ID)).thenReturn(Optional.of(experience));

        experienciaService.deleteExperience(EXPERIENCE_ID);

        verify(experienceRepository).delete(experience);
    }

    @Test
    @DisplayName("Não deve remover experiência de outro usuário")
    void naoDeveRemover_quandoExperienciaDeOutroUsuario() {
        when(experienceRepository.findByExperienceIdAndUser_UserId(EXPERIENCE_ID, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> experienciaService.deleteExperience(EXPERIENCE_ID))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(experienceRepository, never()).delete(any());
    }
}
