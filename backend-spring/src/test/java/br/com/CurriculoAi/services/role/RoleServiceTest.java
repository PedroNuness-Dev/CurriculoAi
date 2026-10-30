package br.com.CurriculoAi.services.role;

import br.com.CurriculoAi.enums.role.RoleType;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.models.Role;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleService")
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private User user;

    @Mock
    private Role role;

    @InjectMocks
    private RoleService roleService;

    @Test
    @DisplayName("Deve adicionar a role ao usuário quando ela existe")
    void deveAdicionarRoleNoUsuario_quandoRoleExiste() {
        // Arrange
        Set<Role> roles = new HashSet<>();

        when(roleRepository.findByType(RoleType.ROLE_CANDIDATO)).thenReturn(Optional.of(role));
        when(user.getRoles()).thenReturn(roles);

        // Act
        User result = roleService.addRoleInUser(user, RoleType.ROLE_CANDIDATO);

        // Assert
        assertThat(result).isSameAs(user);
        assertThat(roles).hasSize(1).contains(role);
        verify(roleRepository).findByType(RoleType.ROLE_CANDIDATO);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando a role não existe")
    void deveLancarResourceNotFound_quandoRoleNaoExiste() {
        // Arrange
        when(roleRepository.findByType(RoleType.ROLE_CANDIDATO)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> roleService.addRoleInUser(user, RoleType.ROLE_CANDIDATO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Role não encontrada");

        verify(roleRepository).findByType(RoleType.ROLE_CANDIDATO);
    }
}