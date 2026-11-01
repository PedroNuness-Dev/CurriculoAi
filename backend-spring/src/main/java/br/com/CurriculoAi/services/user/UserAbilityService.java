package br.com.CurriculoAi.services.user;

import br.com.CurriculoAi.dto.ability.AbilityResponse;
import br.com.CurriculoAi.dto.user.UserAbilityResponse;
import br.com.CurriculoAi.exceptions.ListIsEmptyException;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserAbilityService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final AbilityService abilityService;
    private final AbilityMapper abilityMapper;

    @Transactional
    public UserAbilityResponse registerUserAbilities(List<Long> abilitiesIds){

        if (abilitiesIds.isEmpty()) throw new ListIsEmptyException("Lista de habilidades para vincular ao usuário não pode ser vazia");

        User user = authService.getUserByTokenAuthenticate();

        log.info("Iniciando atribuição de habilidades ao usuário [{}}, com um total de: [{}}", user.getEmail(), abilitiesIds.size());

        Map<Long, Ability> abilitiesMap = getAbilitiesByIds(abilitiesIds);

        List<Ability> abilities = new ArrayList<>(abilitiesMap.values());

        user.setAbilities(abilities);

        userRepository.save(user);

        List<AbilityResponse> abilityResponses = abilities
                .stream()
                .map(abilityMapper::toDto)
                .toList();

        return  new UserAbilityResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                abilityResponses
        );
    }

    private Map<Long, Ability> getAbilitiesByIds(List<Long> abilitiesIds){

        Set<Long> abilitiesIdsUniques = new HashSet<>(abilitiesIds);

        return abilityService.findAbilitiesByIds(abilitiesIdsUniques);
    }
}
