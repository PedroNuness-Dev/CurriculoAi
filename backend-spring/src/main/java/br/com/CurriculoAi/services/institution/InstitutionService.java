package br.com.CurriculoAi.services.institution;

import br.com.CurriculoAi.dto.institution.InstitutionRequest;
import br.com.CurriculoAi.dto.institution.InstitutionResponse;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.institution.InstitutionMapper;
import br.com.CurriculoAi.models.Institution;
import br.com.CurriculoAi.repositories.institution.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final InstitutionMapper institutionMapper;

    @Transactional
    public InstitutionResponse createInstitution(InstitutionRequest institutionRequest) {

        log.info("Iniciando o processo de criação de instituição");

        if (institutionRepository.existsByNameIgnoreCase(institutionRequest.name())) {
            throw new IllegalArgumentException("Já existe uma instituição com esse nome");
        }

        Institution newInstitution = Institution.builder()
                .name(institutionRequest.name())
                .build();

        Institution savedInstitution = institutionRepository.save(newInstitution);

        log.info("Instituição criada com sucesso! Id: {}", savedInstitution.getInstitutionId());

        return institutionMapper.toDto(savedInstitution);
    }

    public List<InstitutionResponse> findAllInstitutions() {

        log.info("Iniciando busca de todas as instituições");

        List<Institution> institutions = institutionRepository.findAll();

        log.info("Instituições buscadas, com um total de: {}", institutions.size());

        return institutions
                .stream()
                .map(institutionMapper::toDto)
                .toList();
    }

    public InstitutionResponse getInstitutionById(Long institutionId) {
        return institutionMapper.toDto(findInstitutionById(institutionId));
    }

    @Transactional
    public InstitutionResponse updateInstitution(Long institutionId, InstitutionRequest institutionRequest) {

        log.info("Iniciando a atualização da instituição com id [{}]", institutionId);

        Institution institution = findInstitutionById(institutionId);

        if (institutionRepository.existsByNameIgnoreCaseAndInstitutionIdNot(institutionRequest.name(), institutionId)) {
            throw new IllegalArgumentException("Já existe uma instituição com esse nome");
        }

        institution.setName(institutionRequest.name());

        Institution updatedInstitution = institutionRepository.save(institution);

        log.info("Instituição atualizada com sucesso! Id: {}", updatedInstitution.getInstitutionId());

        return institutionMapper.toDto(updatedInstitution);
    }

    // Público para ser reutilizado por outros services
    public Institution findInstitutionById(Long institutionId) {
        return institutionRepository.findById(institutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição com id: " + institutionId + " não encontrada"));
    }
}