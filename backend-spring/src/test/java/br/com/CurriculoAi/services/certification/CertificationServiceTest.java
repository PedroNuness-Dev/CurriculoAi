package br.com.CurriculoAi.services.certification;

import br.com.CurriculoAi.dto.certification.CertificationRequest;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.certification.CertificationMapper;
import br.com.CurriculoAi.models.Certification;
import br.com.CurriculoAi.models.Institution;
import br.com.CurriculoAi.repositories.certification.CertificationRepository;
import br.com.CurriculoAi.services.auth.AuthService;
import br.com.CurriculoAi.services.institution.InstitutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CertificationService")
class CertificationServiceTest {

    @Mock
    private CertificationRepository certificationRepository;

    @Mock
    private AuthService authService;

    @Mock
    private InstitutionService institutionService;

    @Mock
    private CertificationMapper certificationMapper;

    @InjectMocks
    private CertificationService certificationService;

    private static final Long USER_ID = 1L;
    private static final Long CERTIFICATION_ID = 30L;

    @BeforeEach
    void setUp() {
        lenient().when(authService.getUserIdByTokenAuthenticate()).thenReturn(USER_ID);
    }

    @Test
    @DisplayName("Deve listar apenas as certificações do usuário autenticado")
    void deveListarCertificacoesDoUsuario() {
        when(certificationRepository.findAllByUser_UserIdOrderByCertificationIdAsc(USER_ID))
                .thenReturn(List.of(Certification.builder().certificationId(CERTIFICATION_ID).build()));

        assertThat(certificationService.findAllCertificationsOfUser()).hasSize(1);
    }

    @Test
    @DisplayName("Deve lançar 404 ao buscar certificação de outro usuário")
    void deveLancarNotFound_quandoCertificacaoDeOutroUsuario() {
        when(certificationRepository.findByCertificationIdAndUser_UserId(CERTIFICATION_ID, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> certificationService.getCertificationById(CERTIFICATION_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar a certificação e recalcular o inProgress")
    void deveAtualizarCertificacao() {
        Certification certification = Certification.builder().certificationId(CERTIFICATION_ID).inProgress(true).build();
        Institution institution = Institution.builder().institutionId(2L).build();

        when(certificationRepository.findByCertificationIdAndUser_UserId(CERTIFICATION_ID, USER_ID)).thenReturn(Optional.of(certification));
        when(institutionService.findInstitutionById(2L)).thenReturn(institution);
        when(certificationRepository.save(certification)).thenReturn(certification);

        certificationService.updateCertification(CERTIFICATION_ID,
                new CertificationRequest("AWS Cloud Practitioner", 2L, LocalDate.of(2025, 5, 1)));

        assertThat(certification.getName()).isEqualTo("AWS Cloud Practitioner");
        assertThat(certification.getInstitution()).isEqualTo(institution);
        assertThat(certification.getInProgress()).isFalse();
    }

    @Test
    @DisplayName("Deve remover a certificação do usuário")
    void deveRemoverCertificacao() {
        Certification certification = Certification.builder().certificationId(CERTIFICATION_ID).build();

        when(certificationRepository.findByCertificationIdAndUser_UserId(CERTIFICATION_ID, USER_ID)).thenReturn(Optional.of(certification));

        certificationService.deleteCertification(CERTIFICATION_ID);

        verify(certificationRepository).delete(certification);
    }

    @Test
    @DisplayName("Não deve remover certificação de outro usuário")
    void naoDeveRemover_quandoCertificacaoDeOutroUsuario() {
        when(certificationRepository.findByCertificationIdAndUser_UserId(CERTIFICATION_ID, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> certificationService.deleteCertification(CERTIFICATION_ID))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(certificationRepository, never()).delete(any());
    }
}
