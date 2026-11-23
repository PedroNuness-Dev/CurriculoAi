package br.com.CurriculoAi.services.user;

import br.com.CurriculoAi.dto.user.UserAbilityResponse;
import br.com.CurriculoAi.exceptions.ListIsEmptyException;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.ability.AbilityMapper;
import br.com.CurriculoAi.models.Ability;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.user.UserRepository;
import br.com.CurriculoAi.services.ability.AbilityService;
import br.com.CurriculoAi.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserAbilityService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final AbilityService abilityService;
    private final AbilityMapper abilityMapper;

    // Adiciona habilidades às que o usuário já possui, sem duplicar
    @Transactional
    public UserAbilityResponse registerUserAbilities(List<Long> abilitiesIds){

        if (abilitiesIds.isEmpty()) throw new ListIsEmptyException("Lista de habilidades para vincular ao usuário não pode ser vazia");

        User user = authService.getUserByTokenAuthenticate();

        log.info("Iniciando adição de habilidades ao usuário [{}], com um total de: [{}]", user.getEmail(), abilitiesIds.size());

        List<Ability> abilitiesToAdd = findAbilitiesOrThrow(abilitiesIds);

        Set<Long> currentAbilitiesIds = user.getAbilities()
                .stream()
                .map(Ability::getAbilityId)
                .collect(Collectors.toSet());

        abilitiesToAdd
                .stream()
                .filter(ability -> !currentAbilitiesIds.contains(ability.getAbilityId()))
                .forEach(user.getAbilities()::add);

        userRepository.save(user);

        log.info("Habilidades adicionadas ao usuário [{}], total atual: [{}]", user.getEmail(), user.getAbilities().size());

        return toResponse(user);
    }

    public UserAbilityResponse getAbilitiesOfUser(){

        User user = authService.getUserByTokenAuthenticate();

        return toResponse(user);
    }

    // Substitui a lista inteira de habilidades do usuário
    @Transactional
    public UserAbilityResponse replaceUserAbilities(List<Long> abilitiesIds){

        if (abilitiesIds.isEmpty()) throw new ListIsEmptyException("Lista de habilidades não pode ser vazia. Para remover todas, use o DELETE");

        User user = authService.getUserByTokenAuthenticate();

        log.info("Iniciando substituição das habilidades do usuário [{}], novo total: [{}]", user.getEmail(), abilitiesIds.size());

        List<Ability> abilities = findAbilitiesOrThrow(abilitiesIds);

        user.getAbilities().clear();
        user.getAbilities().addAll(abilities);

        userRepository.save(user);

        log.info("Habilidades do usuário [{}] substituídas com sucesso", user.getEmail());

        return toResponse(user);
    }

    // Remove todos os vínculos; as habilidades continuam cadastradas
    @Transactional
    public void removeAllUserAbilities(){

        User user = authService.getUserByTokenAuthenticate();

        user.getAbilities().clear();

        userRepository.save(user);

        log.info("Todas as habilidades foram removidas do usuário [{}]", user.getEmail());
    }

    private List<Ability> findAbilitiesOrThrow(List<Long> abilitiesIds){

        Set<Long> abilitiesIdsUniques = new HashSet<>(abilitiesIds);

        Map<Long, Ability> abilitiesMap = abilityService.findAbilitiesByIds(abilitiesIdsUniques);

        Set<Long> notFoundIds = abilitiesIdsUniques
                .stream()
                .filter(id -> !abilitiesMap.containsKey(id))
                .collect(Collectors.toSet());

        if (!notFoundIds.isEmpty()) throw new ResourceNotFoundException("Habilidades com ids: " + notFoundIds + " não encontradas");

        return new ArrayList<>(abilitiesMap.values());
    }

    private UserAbilityResponse toResponse(User user){
        return new UserAbilityResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getAbilities()
                        .stream()
                        .map(abilityMapper::toDto)
                        .toList()
        );
    }
}
