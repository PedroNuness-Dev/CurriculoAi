package br.com.CurriculoAi.repositories.ability;

import br.com.CurriculoAi.models.Ability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbilityRepository extends JpaRepository<Ability, Long> {

    Boolean existsByNameIgnoreCase(String name);
    Boolean existsByNameIgnoreCaseAndAbilityIdNot(String name, Long abilityId);
}
