package br.com.CurriculoAi.services.enterprise;

import br.com.CurriculoAi.dto.enterprise.EnterpriseRequest;
import br.com.CurriculoAi.dto.enterprise.EnterpriseResponse;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.enterprise.EnterpriseMapper;
import br.com.CurriculoAi.models.Enterprise;
import br.com.CurriculoAi.repositories.enterprise.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;
    private final EnterpriseMapper enterpriseMapper;

    @Transactional
    public EnterpriseResponse createEnterprise(EnterpriseRequest enterpriseRequest) {

        log.info("Iniciando o processo de criação de empresa");

        if (enterpriseRepository.existsByNameIgnoreCase(enterpriseRequest.name())) {
            throw new IllegalArgumentException("Já existe uma empresa com esse nome");
        }

        Enterprise newEnterprise = Enterprise.builder()
                .name(enterpriseRequest.name())
                .build();

        Enterprise savedEnterprise = enterpriseRepository.save(newEnterprise);

        log.info("Empresa criada com sucesso! Id: {}", savedEnterprise.getEnterpriseId());

        return enterpriseMapper.toDto(savedEnterprise);
    }

    public List<EnterpriseResponse> findAllEnterprises() {

        log.info("Iniciando busca de todas as empresas");

        List<Enterprise> enterprises = enterpriseRepository.findAll();

        log.info("Empresas buscadas, com um total de: {}", enterprises.size());

        return enterprises
                .stream()
                .map(enterpriseMapper::toDto)
                .toList();
    }

    public EnterpriseResponse getEnterpriseById(Long enterpriseId) {
        return enterpriseMapper.toDto(findEnterpriseById(enterpriseId));
    }

    @Transactional
    public EnterpriseResponse updateEnterprise(Long enterpriseId, EnterpriseRequest enterpriseRequest) {

        log.info("Iniciando a atualização da empresa com id [{}]", enterpriseId);

        Enterprise enterprise = findEnterpriseById(enterpriseId);

        if (enterpriseRepository.existsByNameIgnoreCaseAndEnterpriseIdNot(enterpriseRequest.name(), enterpriseId)) {
            throw new IllegalArgumentException("Já existe uma empresa com esse nome");
        }

        enterprise.setName(enterpriseRequest.name());

        Enterprise updatedEnterprise = enterpriseRepository.save(enterprise);

        log.info("Empresa atualizada com sucesso! Id: {}", updatedEnterprise.getEnterpriseId());

        return enterpriseMapper.toDto(updatedEnterprise);
    }

    // Público para ser reutilizado por outros services
    public Enterprise findEnterpriseById(Long enterpriseId) {
        return enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa com id: " + enterpriseId + " não encontrada"));
    }

    public Map<Long, Enterprise> findAllEnterprisesByIds(Set<Long> enterprisesIds){
        return enterpriseRepository.findAllById(enterprisesIds)
                .stream()
                .collect(Collectors.toMap(Enterprise::getEnterpriseId, Function.identity()));
    }
}