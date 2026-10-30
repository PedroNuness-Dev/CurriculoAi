package br.com.CurriculoAi.services.role;

import br.com.CurriculoAi.enums.role.RoleType;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.models.Role;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;

    public User addRoleInUser(User user, RoleType roleType){

        Role role = roleRepository.findByType(roleType)
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada"));

        user.getRoles().add(role);

        return user;
    }
}
