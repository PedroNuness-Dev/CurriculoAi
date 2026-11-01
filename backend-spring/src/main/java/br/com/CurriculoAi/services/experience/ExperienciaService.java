package br.com.CurriculoAi.services.experience;

import br.com.CurriculoAi.dto.experience.ExperienceRequest;
import br.com.CurriculoAi.dto.experience.ExperienceResponse;
import br.com.CurriculoAi.exceptions.ListIsEmptyException;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.experience.ExperienceMapper;
import br.com.CurriculoAi.models.Area;
import br.com.CurriculoAi.models.Enterprise;
import br.com.CurriculoAi.models.Experience;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.experience.ExperienceRepository;
import br.com.CurriculoAi.services.area.AreaService;
import br.com.CurriculoAi.services.auth.AuthService;
import br.com.CurriculoAi.services.enterprise.EnterpriseService;
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
public class ExperienciaService {

    private final ExperienceRepository experienceRepository;
    private final AreaService areaService;
    private final EnterpriseService enterpriseService;
    private final ExperienceMapper experienceMapper;
    private final AuthService authService;

    @Transactional
    public List<ExperienceResponse> registerExperiences(List<ExperienceRequest> experienceRequests){

        if (experienceRequests.isEmpty()) throw new ListIsEmptyException("Lista de experiências não pode ser vazia");

        log.info("Iniciando cadastro de experiências do usuário, com um total de: {}", experienceRequests.size());

        User user = authService.getUserByTokenAuthenticate();

        Map<Long, Area> areasMap = getAreasByExperiencesRequest(experienceRequests);

        Map<Long, Enterprise> enterprisesMap = getEnterprisesByExperiencesRequest(experienceRequests);

        List<Experience> experiencesForSave = experienceRequests
                .stream()
                .map(experienceRequest -> {

                    Area area = Optional.ofNullable(areasMap.get(experienceRequest.areaId()))
                            .orElseThrow(() -> new ResourceNotFoundException("Área com id: "+experienceRequest.areaId()+" não encontrada"));

                    Enterprise enterprise = Optional.ofNullable(enterprisesMap.get(experienceRequest.enterpriseId()))
                            .orElseThrow(() -> new ResourceNotFoundException("Empresa com id: "+experienceRequest.enterpriseId()+" não encontrada"));

                    Boolean workCurrent = experienceRequest.finishDate() == null;

                    return Experience.builder()
                            .area(area)
                            .enterprise(enterprise)
                            .user(user)
                            .title(experienceRequest.title())
                            .description(experienceRequest.description())
                            .position(experienceRequest.position())
                            .startDate(experienceRequest.startDate())
                            .finishDate(experienceRequest.finishDate())
                            .workCurrent(workCurrent)
                            .build();
                })
                .toList();

        List<Experience> experiencesSaved = experienceRepository.saveAll(experiencesForSave);

        log.info("Cadastro de experiências realizado com sucesso!");

        return experiencesSaved
                .stream()
                .map(experienceMapper::toDto)
                .toList();
    }

    private Map<Long,Area> getAreasByExperiencesRequest(List<ExperienceRequest> experienceRequests){
        Set<Long> areasIds = experienceRequests
                .stream()
                .map(ExperienceRequest::areaId)
                .collect(Collectors.toSet());

        return areaService.findAllAreasByIds(areasIds);
    }

    private Map<Long, Enterprise> getEnterprisesByExperiencesRequest(List<ExperienceRequest> experienceRequests){
        Set<Long> enterprisesIds = experienceRequests
                .stream()
                .map(ExperienceRequest::enterpriseId)
                .collect(Collectors.toSet());

        return enterpriseService.findAllEnterprisesByIds(enterprisesIds);
    }
}
