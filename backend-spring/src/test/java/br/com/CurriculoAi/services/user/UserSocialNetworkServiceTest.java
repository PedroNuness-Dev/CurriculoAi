package br.com.CurriculoAi.services.user;

import br.com.CurriculoAi.dto.user.UserSocialNetworkRequest;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.user.UserSocialNetworkMapper;
import br.com.CurriculoAi.models.SocialNetwork;
import br.com.CurriculoAi.models.UserSocialNetwork;
import br.com.CurriculoAi.repositories.user.UserSocialNetworkRepository;
import br.com.CurriculoAi.services.auth.AuthService;
import br.com.CurriculoAi.services.social_network.SocialNetworkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserSocialNetworkService")
class UserSocialNetworkServiceTest {

    @Mock
    private UserSocialNetworkRepository userSocialNetworkRepository;

    @Mock
    private AuthService authService;

    @Mock
    private SocialNetworkService socialNetworkService;

    @Mock
    private UserSocialNetworkMapper userSocialNetworkMapper;

    @InjectMocks
    private UserSocialNetworkService userSocialNetworkService;

    private static final Long USER_ID = 1L;
    private static final Long USER_SOCIAL_NETWORK_ID = 20L;

    @BeforeEach
    void setUp() {
        lenient().when(authService.getUserIdByTokenAuthenticate()).thenReturn(USER_ID);
    }

    @Test
    @DisplayName("Deve listar apenas as redes sociais do usuário autenticado")
    void deveListarRedesDoUsuario() {
        UserSocialNetwork usn = UserSocialNetwork.builder().userSocialNetworkId(USER_SOCIAL_NETWORK_ID).build();

        when(userSocialNetworkRepository.findAllUserSocialNetworksOfUser(USER_ID)).thenReturn(List.of(usn));

        assertThat(userSocialNetworkService.getUserSocialNetworksOfUser()).hasSize(1);
        verify(userSocialNetworkRepository).findAllUserSocialNetworksOfUser(USER_ID);
    }

    @Test
    @DisplayName("Deve lançar 404 quando a rede social não pertence ao usuário")
    void deveLancarNotFound_quandoRedeNaoPertenceAoUsuario() {
        when(userSocialNetworkRepository.findByUserSocialNetworkIdAndUser_UserId(USER_SOCIAL_NETWORK_ID, USER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userSocialNetworkService.getUserSocialNetworkById(USER_SOCIAL_NETWORK_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar link e rede social do usuário")
    void deveAtualizarLinkERede() {
        UserSocialNetwork usn = UserSocialNetwork.builder()
                .userSocialNetworkId(USER_SOCIAL_NETWORK_ID)
                .link("https://antigo.com")
                .build();
        SocialNetwork newSocialNetwork = SocialNetwork.builder().socialNetworkId(3L).build();

        when(userSocialNetworkRepository.findByUserSocialNetworkIdAndUser_UserId(USER_SOCIAL_NETWORK_ID, USER_ID))
                .thenReturn(Optional.of(usn));
        when(socialNetworkService.findSocialNetworkById(3L)).thenReturn(newSocialNetwork);
        when(userSocialNetworkRepository.save(usn)).thenReturn(usn);

        userSocialNetworkService.updateUserSocialNetwork(USER_SOCIAL_NETWORK_ID,
                new UserSocialNetworkRequest(3L, "https://novo.com"));

        assertThat(usn.getLink()).isEqualTo("https://novo.com");
        assertThat(usn.getSocialNetwork()).isEqualTo(newSocialNetwork);
    }

    @Test
    @DisplayName("Deve remover a rede social quando ela pertence ao usuário")
    void deveRemoverRede_quandoPertenceAoUsuario() {
        UserSocialNetwork usn = UserSocialNetwork.builder().userSocialNetworkId(USER_SOCIAL_NETWORK_ID).build();

        when(userSocialNetworkRepository.findByUserSocialNetworkIdAndUser_UserId(USER_SOCIAL_NETWORK_ID, USER_ID))
                .thenReturn(Optional.of(usn));

        userSocialNetworkService.deleteUserSocialNetwork(USER_SOCIAL_NETWORK_ID);

        verify(userSocialNetworkRepository).delete(usn);
    }

    @Test
    @DisplayName("Não deve remover quando a rede social não pertence ao usuário")
    void naoDeveRemover_quandoRedeNaoPertenceAoUsuario() {
        when(userSocialNetworkRepository.findByUserSocialNetworkIdAndUser_UserId(USER_SOCIAL_NETWORK_ID, USER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userSocialNetworkService.deleteUserSocialNetwork(USER_SOCIAL_NETWORK_ID))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userSocialNetworkRepository, never()).delete(any());
    }
}
