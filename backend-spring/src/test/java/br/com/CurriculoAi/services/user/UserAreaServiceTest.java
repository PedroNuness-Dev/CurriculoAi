package br.com.CurriculoAi.services.user;

import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.models.Area;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.user.UserRepository;
import br.com.CurriculoAi.services.area.AreaService;
import br.com.CurriculoAi.services.auth.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAreaService")
class UserAreaServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AreaService areaService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserAreaService userAreaService;

    @Test
    @DisplayName("Deve retornar a área vinculada ao usuário")
    void deveRetornarArea() {
        Area area = Area.builder().areaId(2L).name("Backend").build();
        User user = User.builder().userId(1L).area(area).build();

        when(authService.getUserByTokenAuthenticate()).thenReturn(user);

        assertThat(userAreaService.getAreaOfUser().area().areaId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Deve lançar 404 ao buscar área quando o usuário não possui área")
    void deveLancarNotFound_quandoUsuarioSemArea() {
        when(authService.getUserByTokenAuthenticate()).thenReturn(User.builder().userId(1L).build());

        assertThatThrownBy(() -> userAreaService.getAreaOfUser())
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve trocar a área do usuário")
    void deveTrocarArea() {
        User user = User.builder().userId(1L).area(Area.builder().areaId(2L).build()).build();
        Area newArea = Area.builder().areaId(3L).name("Dados").build();

        when(authService.getUserByTokenAuthenticate()).thenReturn(user);
        when(areaService.findAreaById(3L)).thenReturn(newArea);

        userAreaService.updateAreaOfUser(3L);

        assertThat(user.getArea()).isEqualTo(newArea);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deve desvincular a área do usuário")
    void deveDesvincularArea() {
        User user = User.builder().userId(1L).area(Area.builder().areaId(2L).build()).build();

        when(authService.getUserByTokenAuthenticate()).thenReturn(user);

        userAreaService.removeAreaOfUser();

        assertThat(user.getArea()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Não deve salvar ao remover área quando o usuário não possui área")
    void naoDeveSalvar_quandoRemoverSemArea() {
        when(authService.getUserByTokenAuthenticate()).thenReturn(User.builder().userId(1L).build());

        assertThatThrownBy(() -> userAreaService.removeAreaOfUser())
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).save(any());
    }
}
