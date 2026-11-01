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
public class CertificacaoService {

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
                            .certificate(certificationRequest.certificate())
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
}
