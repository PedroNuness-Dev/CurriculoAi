package br.com.CurriculoAi.services.user;

import br.com.CurriculoAi.dto.user.UserLanguageRequest;
import br.com.CurriculoAi.dto.user.UserLanguageResponse;
import br.com.CurriculoAi.enums.language.LanguageLevel;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.user.UserLanguageMapper;
import br.com.CurriculoAi.models.Language;
import br.com.CurriculoAi.models.UserLanguage;
import br.com.CurriculoAi.repositories.user.UserLanguageRepository;
import br.com.CurriculoAi.services.auth.AuthService;
import br.com.CurriculoAi.services.language.LanguageService;
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
@DisplayName("UserLanguageService")
class UserLanguageServiceTest {

    @Mock
    private UserLanguageRepository userLanguageRepository;

    @Mock
    private AuthService authService;

    @Mock
    private LanguageService languageService;

    @Mock
    private UserLanguageMapper userLanguageMapper;

    @InjectMocks
    private UserLanguageService userLanguageService;

    private static final Long USER_ID = 1L;
    private static final Long USER_LANGUAGE_ID = 10L;

    @BeforeEach
    void setUp() {
        lenient().when(authService.getUserIdByTokenAuthenticate()).thenReturn(USER_ID);
    }

    @Test
    @DisplayName("Deve buscar o idioma quando ele pertence ao usuário autenticado")
    void deveBuscarIdioma_quandoPertenceAoUsuario() {
        UserLanguage userLanguage = UserLanguage.builder().userLanguageId(USER_LANGUAGE_ID).build();
        UserLanguageResponse response = new UserLanguageResponse(USER_LANGUAGE_ID, LanguageLevel.BASICO, null);

        when(userLanguageRepository.findByUserLanguageIdAndUser_UserId(USER_LANGUAGE_ID, USER_ID))
                .thenReturn(Optional.of(userLanguage));
        when(userLanguageMapper.toDto(userLanguage)).thenReturn(response);

        assertThat(userLanguageService.getUserLanguageById(USER_LANGUAGE_ID)).isEqualTo(response);
    }

    @Test
    @DisplayName("Deve lançar 404 quando o idioma não pertence ao usuário autenticado")
    void deveLancarNotFound_quandoIdiomaNaoPertenceAoUsuario() {
        when(userLanguageRepository.findByUserLanguageIdAndUser_UserId(USER_LANGUAGE_ID, USER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userLanguageService.getUserLanguageById(USER_LANGUAGE_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar idioma e nível do idioma do usuário")
    void deveAtualizarIdiomaENivel() {
        UserLanguage userLanguage = UserLanguage.builder()
                .userLanguageId(USER_LANGUAGE_ID)
                .level(LanguageLevel.BASICO)
                .build();
        Language newLanguage = Language.builder().languageId(5L).build();

        when(userLanguageRepository.findByUserLanguageIdAndUser_UserId(USER_LANGUAGE_ID, USER_ID))
                .thenReturn(Optional.of(userLanguage));
        when(languageService.findLanguageById(5L)).thenReturn(newLanguage);
        when(userLanguageRepository.save(userLanguage)).thenReturn(userLanguage);

        userLanguageService.updateUserLanguage(USER_LANGUAGE_ID, new UserLanguageRequest(5L, "fluente"));

        assertThat(userLanguage.getLanguage()).isEqualTo(newLanguage);
        assertThat(userLanguage.getLevel()).isEqualTo(LanguageLevel.FLUENTE);
        verify(userLanguageRepository).save(userLanguage);
    }

    @Test
    @DisplayName("Não deve atualizar quando o idioma não pertence ao usuário")
    void naoDeveAtualizar_quandoIdiomaNaoPertenceAoUsuario() {
        when(userLanguageRepository.findByUserLanguageIdAndUser_UserId(USER_LANGUAGE_ID, USER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userLanguageService.updateUserLanguage(USER_LANGUAGE_ID, new UserLanguageRequest(5L, "fluente")))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userLanguageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve remover o idioma quando ele pertence ao usuário")
    void deveRemoverIdioma_quandoPertenceAoUsuario() {
        UserLanguage userLanguage = UserLanguage.builder().userLanguageId(USER_LANGUAGE_ID).build();

        when(userLanguageRepository.findByUserLanguageIdAndUser_UserId(USER_LANGUAGE_ID, USER_ID))
                .thenReturn(Optional.of(userLanguage));

        userLanguageService.deleteUserLanguage(USER_LANGUAGE_ID);

        verify(userLanguageRepository).delete(userLanguage);
    }

    @Test
    @DisplayName("Não deve remover quando o idioma não pertence ao usuário")
    void naoDeveRemover_quandoIdiomaNaoPertenceAoUsuario() {
        when(userLanguageRepository.findByUserLanguageIdAndUser_UserId(USER_LANGUAGE_ID, USER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userLanguageService.deleteUserLanguage(USER_LANGUAGE_ID))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userLanguageRepository, never()).delete(any());
    }
}
