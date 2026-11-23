package br.com.CurriculoAi.services.certification;

import br.com.CurriculoAi.dto.certification.CertificationRequest;
import br.com.CurriculoAi.dto.certification.CertificationResponse;
import br.com.CurriculoAi.exceptions.ListIsEmptyException;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.certification.CertificationMapper;
import br.com.CurriculoAi.models.Certification;
import br.com.CurriculoAi.models.Institution;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.certification.CertificationRepository;
import br.com.CurriculoAi.services.auth.AuthService;
import br.com.CurriculoAi.services.institution.InstitutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final AuthService authService;
    private final InstitutionService institutionService;
    private final CertificationMapper certificationMapper;

    @Transactional
    public List<CertificationResponse> registerCertifications(List<CertificationRequest> certificationRequests){

        if (certificationRequests.isEmpty()) throw new ListIsEmptyException("Lista de certificações não pode ser vazia");

        log.info("Iniando o cadastro de formações do usuário, quantidade de formações: {}", certificationRequests.size());

        User user = authService.getUserByTokenAuthenticate();

        Map<Long, Institution> instituicoesPorId = getInstitutionsByCertificationsRequest(certificationRequests);

        List<Certification> certificationsForSave = certificationRequests
                .stream()
                .map(certificationRequest -> {

                    Institution institution = Optional.ofNullable(instituicoesPorId.get(certificationRequest.institutionId()))
                            .orElseThrow(() -> new ResourceNotFoundException("Instituição com id: "+certificationRequest.institutionId()+" não encontrada"));

                    Boolean inProgress = certificationRequest.finishDate() == null;

                    return Certification.builder()
                            .name(certificationRequest.name())
                            .finishDate(certificationRequest.finishDate())
                            .inProgress(inProgress)
                            .institution(institution)
                            .user(user)
                            .build();
                })
                .toList();

        List<Certification> certificationsSaved = certificationRepository.saveAll(certificationsForSave);

        log.info("Certificações salvas com sucesso!");

        return certificationsSaved
                .stream()
                .map(certificationMapper::toDto)
                .toList();
    }

    public Map<Long, Institution> getInstitutionsByCertificationsRequest(List<CertificationRequest> certificationRequests){
        Set<Long> institutionsIds = certificationRequests
                .stream()
                .map(CertificationRequest::institutionId)
                .collect(Collectors.toSet());
        return institutionService.findAllByIds(institutionsIds);
    }


    public List<CertificationResponse> findAllCertificationsOfUser(){

        Long userId = authService.getUserIdByTokenAuthenticate();

        return certificationRepository.findAllByUser_UserIdOrderByCertificationIdAsc(userId)
                .stream()
                .map(certificationMapper::toDto)
                .toList();
    }

    public CertificationResponse getCertificationById(Long certificationId){

        Certification certification = findCertificationOfAuthenticatedUser(certificationId);

        return certificationMapper.toDto(certification);
    }

    @Transactional
    public CertificationResponse updateCertification(Long certificationId, CertificationRequest certificationRequest){

        log.info("Iniciando atualização da certificação com id [{}]", certificationId);

        Certification certification = findCertificationOfAuthenticatedUser(certificationId);

        Institution institution = institutionService.findInstitutionById(certificationRequest.institutionId());

        certification.setName(certificationRequest.name());
        certification.setInstitution(institution);
        certification.setFinishDate(certificationRequest.finishDate());
        certification.setInProgress(certificationRequest.finishDate() == null);

        Certification updatedCertification = certificationRepository.save(certification);

        log.info("Certificação com id [{}] atualizada com sucesso", certificationId);

        return certificationMapper.toDto(updatedCertification);
    }

    @Transactional
    public void deleteCertification(Long certificationId){

        log.info("Iniciando remoção da certificação com id [{}]", certificationId);

        Certification certification = findCertificationOfAuthenticatedUser(certificationId);

        certificationRepository.delete(certification);

        log.info("Certificação com id [{}] removida com sucesso", certificationId);
    }

    // Busca já filtrando pelo usuário autenticado: se a certificação for de outro usuário, retorna 404
    private Certification findCertificationOfAuthenticatedUser(Long certificationId){

        Long userId = authService.getUserIdByTokenAuthenticate();

        return certificationRepository.findByCertificationIdAndUser_UserId(certificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificação com id: "+certificationId+" não encontrada"));
    }
}
