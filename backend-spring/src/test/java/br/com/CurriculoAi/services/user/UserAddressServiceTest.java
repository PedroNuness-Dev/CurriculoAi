package br.com.CurriculoAi.services.user;

import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.models.Address;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.user.UserRepository;
import br.com.CurriculoAi.services.address.AddressService;
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
@DisplayName("UserAddressService")
class UserAddressServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private UserAddressService userAddressService;

    @Test
    @DisplayName("Deve lançar 404 ao buscar endereço quando o usuário não possui endereço")
    void deveLancarNotFound_quandoUsuarioSemEndereco() {
        when(authService.getUserByTokenAuthenticate()).thenReturn(User.builder().userId(1L).build());

        assertThatThrownBy(() -> userAddressService.getAddressOfUser())
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve trocar o endereço do usuário pelo novo cep")
    void deveTrocarEndereco() {
        User user = User.builder().userId(1L).address(Address.builder().addressId(1L).build()).build();
        Address newAddress = Address.builder().addressId(2L).zipCode("01001000").build();

        when(authService.getUserByTokenAuthenticate()).thenReturn(user);
        when(addressService.getDetailsByZipCode("01001000")).thenReturn(newAddress);

        userAddressService.updateAddressOfUserByZipCode("01001000");

        assertThat(user.getAddress()).isEqualTo(newAddress);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deve desvincular o endereço do usuário")
    void deveDesvincularEndereco() {
        User user = User.builder().userId(1L).address(Address.builder().addressId(1L).build()).build();

        when(authService.getUserByTokenAuthenticate()).thenReturn(user);

        userAddressService.removeAddressOfUser();

        assertThat(user.getAddress()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Não deve salvar ao remover endereço quando o usuário não possui endereço")
    void naoDeveSalvar_quandoRemoverSemEndereco() {
        when(authService.getUserByTokenAuthenticate()).thenReturn(User.builder().userId(1L).build());

        assertThatThrownBy(() -> userAddressService.removeAddressOfUser())
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).save(any());
    }
}
