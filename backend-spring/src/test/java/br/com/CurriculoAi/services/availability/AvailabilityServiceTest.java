package br.com.CurriculoAi.services.availability;

import br.com.CurriculoAi.dto.availability.AvailabilityRequest;
import br.com.CurriculoAi.enums.availability.WorkModel;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.availability.AvailabilityMapper;
import br.com.CurriculoAi.models.Availability;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.availability.AvailabilityRepository;
import br.com.CurriculoAi.services.auth.AuthService;
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
@DisplayName("AvailabilityService")
class AvailabilityServiceTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private AuthService authService;

    @Mock
    private AvailabilityMapper availabilityMapper;

    @InjectMocks
    private AvailabilityService availabilityService;

    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        lenient().when(authService.getUserIdByTokenAuthenticate()).thenReturn(USER_ID);
    }

    @Test
    @DisplayName("POST deve recusar quando o usuário já possui disponibilidade")
    void naoDeveCadastrar_quandoJaPossuiDisponibilidade() {
        when(authService.getUserByTokenAuthenticate()).thenReturn(User.builder().userId(USER_ID).build());
        when(availabilityRepository.existsByUser_UserId(USER_ID)).thenReturn(true);

        assertThatThrownBy(() -> availabilityService.registerAvailbilityUser(new AvailabilityRequest(LocalDate.now(), "REMOTO")))
                .isInstanceOf(IllegalArgumentException.class);

        verify(availabilityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar 404 ao buscar quando o usuário não possui disponibilidade")
    void deveLancarNotFound_quandoSemDisponibilidade() {
        when(availabilityRepository.findByUser_UserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> availabilityService.getAvailabilityOfUser())
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar a disponibilidade do usuário")
    void deveAtualizarDisponibilidade() {
        Availability availability = Availability.builder().availabilityId(7L).workModel(WorkModel.PRESENCIAL).build();
        LocalDate start = LocalDate.of(2027, 1, 10);

        when(availabilityRepository.findByUser_UserId(USER_ID)).thenReturn(Optional.of(availability));
        when(availabilityRepository.save(availability)).thenReturn(availability);

        availabilityService.updateAvailabilityOfUser(new AvailabilityRequest(start, "hibrido"));

        assertThat(availability.getWorkModel()).isEqualTo(WorkModel.HIBRIDO);
        assertThat(availability.getStartAvailability()).isEqualTo(start);
    }

    @Test
    @DisplayName("Deve remover a disponibilidade do usuário")
    void deveRemoverDisponibilidade() {
        Availability availability = Availability.builder().availabilityId(7L).build();

        when(availabilityRepository.findByUser_UserId(USER_ID)).thenReturn(Optional.of(availability));

        availabilityService.deleteAvailabilityOfUser();

        verify(availabilityRepository).delete(availability);
    }
}
