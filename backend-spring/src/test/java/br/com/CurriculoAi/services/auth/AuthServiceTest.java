package br.com.CurriculoAi.services.auth;

import br.com.CurriculoAi.dto.auth.LoginRequest;
import br.com.CurriculoAi.dto.auth.LoginResponse;
import br.com.CurriculoAi.dto.auth.TokenDetails;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.user.UserRepository;
import br.com.CurriculoAi.security.TokenService;
import br.com.CurriculoAi.security.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetails;

    @InjectMocks
    private AuthService authService;

    private static final Long USER_ID = 1L;
    private static final String NAME = "João Silva";
    private static final String EMAIL = "joao@email.com";
    private static final String PASSWORD = "senha123";
    private static final String TOKEN = "jwt-token";

    @AfterEach
    void limparSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ---------------------------------------------------------------
    // authenticateUser
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve autenticar o usuário, gerar o token e retornar LoginResponse")
    void deveAutenticarUsuarioComSucesso() {
        // Arrange
        LoginRequest request = new LoginRequest(EMAIL, PASSWORD);
        TokenDetails tokenDetails = org.mockito.Mockito.mock(TokenDetails.class);

        when(tokenDetails.token()).thenReturn(TOKEN);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenService.generateToken(userDetails)).thenReturn(tokenDetails);
        when(userDetails.getUserId()).thenReturn(USER_ID);
        when(userDetails.getName()).thenReturn(NAME);
        when(userDetails.getEmail()).thenReturn(EMAIL);

        // Act
        LoginResponse response = authService.authenticateUser(request);

        // Assert
        LoginResponse expected = new LoginResponse(
                TOKEN, "Bearer", tokenDetails.expiration(), USER_ID, NAME, EMAIL);

        assertThat(response).isEqualTo(expected);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(authentication);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertThat(captor.getValue().getPrincipal()).isEqualTo(EMAIL);
        assertThat(captor.getValue().getCredentials()).isEqualTo(PASSWORD);

        verify(tokenService).generateToken(userDetails);
    }

    @Test
    @DisplayName("Deve propagar BadCredentialsException e não gerar token quando as credenciais são inválidas")
    void devePropagarExcecao_quandoCredenciaisInvalidas() {
        // Arrange
        LoginRequest request = new LoginRequest(EMAIL, "senha-errada");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        // Act & Assert
        assertThatThrownBy(() -> authService.authenticateUser(request))
                .isInstanceOf(BadCredentialsException.class);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(tokenService);
    }

    // ---------------------------------------------------------------
    // getUserIdByTokenAuthenticate
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar o id do usuário autenticado no contexto de segurança")
    void deveRetornarIdDoUsuarioAutenticado() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(USER_ID);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        Long result = authService.getUserIdByTokenAuthenticate();

        // Assert
        assertThat(result).isEqualTo(USER_ID);
    }

    @Test
    @DisplayName("Deve lançar NullPointerException ao buscar o id quando não há autenticação no contexto")
    void deveLancarExcecao_quandoNaoHaAutenticacao_aoBuscarId() {
        // Arrange
        SecurityContextHolder.clearContext();

        // Act & Assert
        assertThatThrownBy(() -> authService.getUserIdByTokenAuthenticate())
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve lançar NullPointerException ao buscar o id quando o principal é nulo")
    void deveLancarExcecao_quandoPrincipalNulo_aoBuscarId() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert
        assertThatThrownBy(() -> authService.getUserIdByTokenAuthenticate())
                .isInstanceOf(NullPointerException.class);
    }

    // ---------------------------------------------------------------
    // getUserByTokenAuthenticate
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar o usuário autenticado quando ele existe no banco")
    void deveRetornarUsuarioAutenticado() {
        // Arrange
        User user = User.builder().userId(USER_ID).name(NAME).email(EMAIL).build();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        User result = authService.getUserByTokenAuthenticate();

        // Assert
        assertThat(result).isSameAs(user);
        verify(userRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o usuário autenticado não existe no banco")
    void deveLancarResourceNotFound_quandoUsuarioAutenticadoNaoExiste() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert
        assertThatThrownBy(() -> authService.getUserByTokenAuthenticate())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário authenticado não encontrado");
    }

    @Test
    @DisplayName("Deve lançar NullPointerException ao buscar o usuário quando não há autenticação no contexto")
    void deveLancarExcecao_quandoNaoHaAutenticacao_aoBuscarUsuario() {
        // Arrange
        SecurityContextHolder.clearContext();

        // Act & Assert
        assertThatThrownBy(() -> authService.getUserByTokenAuthenticate())
                .isInstanceOf(NullPointerException.class);

        verify(userRepository, never()).findById(any());
    }
}