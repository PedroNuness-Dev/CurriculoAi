package br.com.CurriculoAi.repositories.role;

import br.com.CurriculoAi.enums.role.RoleType;
import br.com.CurriculoAi.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByType(RoleType roleType);
}
