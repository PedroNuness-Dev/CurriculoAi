package br.com.CurriculoAi.services.ability;

import br.com.CurriculoAi.dto.ability.AbilityRequest;
import br.com.CurriculoAi.dto.ability.AbilityResponse;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.ability.AbilityMapper;
import br.com.CurriculoAi.models.Ability;
import br.com.CurriculoAi.repositories.ability.AbilityRepository;
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
public class AbilityService {

    private final AbilityRepository abilityRepository;
    private final AbilityMapper abilityMapper;

    @Transactional
    public AbilityResponse createAbility(AbilityRequest abilityRequest) {

        log.info("Iniciando o processo de criação de habilidade");

        if (abilityRepository.existsByNameIgnoreCase(abilityRequest.name())) {
            throw new IllegalArgumentException("Já existe uma habilidade com esse nome");
        }

        Ability newAbility = Ability.builder()
                .name(abilityRequest.name())
                .build();

        Ability savedAbility = abilityRepository.save(newAbility);

        log.info("Habilidade criada com sucesso! Id: {}", savedAbility.getAbilityId());

        return abilityMapper.toDto(savedAbility);
    }

    public List<AbilityResponse> findAllAbilities() {

        log.info("Iniciando busca de todas as habilidades");

        List<Ability> abilities = abilityRepository.findAll();

        log.info("Habilidades buscadas, com um total de: {}", abilities.size());

        return abilities
                .stream()
                .map(abilityMapper::toDto)
                .toList();
    }

    public AbilityResponse getAbilityById(Long abilityId) {
        return abilityMapper.toDto(findAbilityById(abilityId));
    }

    @Transactional
    public AbilityResponse updateAbility(Long abilityId, AbilityRequest abilityRequest) {

        log.info("Iniciando a atualização da habilidade com id [{}]", abilityId);

        Ability ability = findAbilityById(abilityId);

        if (abilityRepository.existsByNameIgnoreCaseAndAbilityIdNot(abilityRequest.name(), abilityId)) {
            throw new IllegalArgumentException("Já existe uma habilidade com esse nome");
        }

        ability.setName(abilityRequest.name());

        Ability updatedAbility = abilityRepository.save(ability);

        log.info("Habilidade atualizada com sucesso! Id: {}", updatedAbility.getAbilityId());

        return abilityMapper.toDto(updatedAbility);
    }

    // Público para ser reutilizado por outros services (ex: UserAbilityService)
    public Ability findAbilityById(Long abilityId) {
        return abilityRepository.findById(abilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Habilidade com id: " + abilityId + " não encontrada"));
    }

    public Map<Long, Ability> findAbilitiesByIds(Set<Long> abilitiesIds){
        return abilityRepository.findAllById(abilitiesIds)
                .stream()
                .collect(Collectors.toMap(Ability::getAbilityId, Function.identity()));
    }
}