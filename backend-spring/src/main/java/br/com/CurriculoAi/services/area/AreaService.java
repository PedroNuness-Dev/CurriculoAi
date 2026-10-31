package br.com.CurriculoAi.services.area;

import br.com.CurriculoAi.dto.area.AreaRequest;
import br.com.CurriculoAi.dto.area.AreaResponse;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.area.AreaMapper;
import br.com.CurriculoAi.models.Area;
import br.com.CurriculoAi.repositories.area.AreaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AreaService {

    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;

    @Transactional
    public AreaResponse createArea(AreaRequest areaRequest) {

        log.info("Iniciando o processo de criação de área");

        if (areaRepository.existsByNameIgnoreCase(areaRequest.name())) {
            throw new IllegalArgumentException("Já existe uma área com esse nome");
        }

        Area newArea = Area.builder()
                .name(areaRequest.name())
                .build();

        Area savedArea = areaRepository.save(newArea);

        log.info("Área criada com sucesso! Id: {}", savedArea.getAreaId());

        return areaMapper.toDTO(savedArea);
    }

    public List<AreaResponse> findAllAreas() {
        return areaRepository.findAll()
                .stream()
                .map(areaMapper::toDTO)
                .toList();
    }

    public AreaResponse getAreaById(Long areaId) {
        return areaMapper.toDTO(findAreaById(areaId));
    }

    @Transactional
    public AreaResponse updateArea(Long areaId, AreaRequest areaRequest) {

        log.info("Iniciando a atualização da área com id [{}]", areaId);

        Area area = findAreaById(areaId);

        if (areaRepository.existsByNameIgnoreCaseAndAreaIdNot(areaRequest.name(), areaId)) {
            throw new IllegalArgumentException("Já existe uma área com esse nome");
        }

        area.setName(areaRequest.name());

        Area updatedArea = areaRepository.save(area);

        log.info("Área atualizada com sucesso! Id: {}", updatedArea.getAreaId());

        return areaMapper.toDTO(updatedArea);
    }

    // Público para ser reutilizado por outros services (ex: UserAreaService)
    public Area findAreaById(Long areaId) {
        return areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException("Área com id: "+areaId+" não encontrada para adição"));
    }
}