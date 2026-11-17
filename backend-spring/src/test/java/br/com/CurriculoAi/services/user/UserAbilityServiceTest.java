package br.com.CurriculoAi.services.user;

import br.com.CurriculoAi.exceptions.ListIsEmptyException;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.ability.AbilityMapper;
import br.com.CurriculoAi.models.Ability;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.user.UserRepository;
import br.com.CurriculoAi.services.ability.AbilityService;
import br.com.CurriculoAi.services.auth.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAbilityService")
class UserAbilityServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private AbilityService abilityService;

    @Mock
    private AbilityMapper abilityMapper;

    @InjectMocks
    private UserAbilityService userAbilityService;

    private final Ability java = Ability.builder().abilityId(1L).name("Java").build();
    private final Ability docker = Ability.builder().abilityId(2L).name("Docker").build();
    private final Ability spring = Ability.builder().abilityId(3L).name("Spring").build();

    private User userWith(Ability... abilities) {
        return User.builder().userId(1L).abilities(new ArrayList<>(List.of(abilities))).build();
    }

    @Test
    @DisplayName("POST deve adicionar sem apagar as existentes e sem duplicar")
    void deveAdicionarSemDuplicar() {
        User user = userWith(java, docker);

        when(authService.getUserByTokenAuthenticate()).thenReturn(user);
        when(abilityService.findAbilitiesByIds(Set.of(2L, 3L))).thenReturn(Map.of(2L, docker, 3L, spring));

        userAbilityService.registerUserAbilities(List.of(2L, 3L));

        assertThat(user.getAbilities()).containsExactlyInAnyOrder(java, docker, spring);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("PUT deve substituir a lista inteira")
    void deveSubstituirLista() {
        User user = userWith(java, docker);

        when(authService.getUserByTokenAuthenticate()).thenReturn(user);
        when(abilityService.findAbilitiesByIds(Set.of(3L))).thenReturn(Map.of(3L, spring));

        userAbilityService.replaceUserAbilities(List.of(3L));

        assertThat(user.getAbilities()).containsExactly(spring);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("PUT com lista vazia deve lançar exceção")
    void deveLancarExcecao_quandoPutComListaVazia() {
        assertThatThrownBy(() -> userAbilityService.replaceUserAbilities(List.of()))
                .isInstanceOf(ListIsEmptyException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar 404 quando algum id de habilidade não existe")
    void deveLancarNotFound_quandoHabilidadeNaoExiste() {
        User user = userWith(java);

        when(authService.getUserByTokenAuthenticate()).thenReturn(user);
        when(abilityService.findAbilitiesByIds(Set.of(3L, 99L))).thenReturn(Map.of(3L, spring));

        assertThatThrownBy(() -> userAbilityService.replaceUserAbilities(List.of(3L, 99L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        assertThat(user.getAbilities()).containsExactly(java);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("DELETE deve remover todas as habilidades do usuário")
    void deveRemoverTodas() {
        User user = userWith(java, docker);

        when(authService.getUserByTokenAuthenticate()).thenReturn(user);

        userAbilityService.removeAllUserAbilities();

        assertThat(user.getAbilities()).isEmpty();
        verify(userRepository).save(user);
    }
}
