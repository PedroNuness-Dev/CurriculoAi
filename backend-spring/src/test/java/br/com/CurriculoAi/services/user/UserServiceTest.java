package br.com.CurriculoAi.services.user;

import br.com.CurriculoAi.dto.user.UserRegisterRequest;
import br.com.CurriculoAi.dto.user.UserResponse;
import br.com.CurriculoAi.enums.role.RoleType;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.user.UserRepository;
import br.com.CurriculoAi.services.role.RoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    private static final String NAME = "João Silva";
    private static final String EMAIL = "joao@email.com";
    private static final String RAW_PASSWORD = "senha123";
    private static final String ENCODED_PASSWORD = "senha-criptografada";

    // ATENÇÃO: ajuste a ordem dos parâmetros conforme o seu record UserRegisterRequest
    private UserRegisterRequest buildRequest() {
        return new UserRegisterRequest(NAME, EMAIL, RAW_PASSWORD);
    }

    @Test
    @DisplayName("Deve registrar usuário com role de candidato quando o email não existe")
    void deveRegistrarUsuarioComSucesso_quandoEmailNaoExiste() {
        // Arrange
        UserRegisterRequest request = buildRequest();

        User savedUser = User.builder()
                .userId(1L)
                .name(NAME)
                .email(EMAIL)
                .password(ENCODED_PASSWORD)
                .build();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(roleService.addRoleInUser(any(User.class), eq(RoleType.ROLE_CANDIDATO)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponse response = userService.registerUserWithRoleTypeCandidate(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo(NAME);
        assertThat(response.email()).isEqualTo(EMAIL);
        assertThat(response.roles()).isEqualTo(savedUser.getRoles());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User userToSave = userCaptor.getValue();
        assertThat(userToSave.getName()).isEqualTo(NAME);
        assertThat(userToSave.getEmail()).isEqualTo(EMAIL);
        assertThat(userToSave.getPassword()).isEqualTo(ENCODED_PASSWORD);

        verify(userRepository).findByEmail(EMAIL);
        verify(passwordEncoder).encode(RAW_PASSWORD);
        verify(roleService).addRoleInUser(any(User.class), eq(RoleType.ROLE_CANDIDATO));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o email já está cadastrado")
    void deveLancarExcecao_quandoEmailJaCadastrado() {
        // Arrange
        UserRegisterRequest request = buildRequest();
        User existingUser = User.builder().userId(99L).email(EMAIL).build();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUserWithRoleTypeCandidate(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email inválido para cadastro");

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder, roleService);
    }

    @Test
    @DisplayName("Deve montar o usuário com senha criptografada e role de candidato")
    void deveConstruirUsuarioComRoleCandidato() {
        // Arrange
        UserRegisterRequest request = buildRequest();

        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(roleService.addRoleInUser(any(User.class), eq(RoleType.ROLE_CANDIDATO)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.buildUserWithRoleCandidate(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(result.getPassword()).isNotEqualTo(RAW_PASSWORD);

        verify(passwordEncoder).encode(RAW_PASSWORD);
        verify(roleService).addRoleInUser(any(User.class), eq(RoleType.ROLE_CANDIDATO));
    }
}