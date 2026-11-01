package br.com.CurriculoAi.dto.user;

import br.com.CurriculoAi.dto.role.RoleResponse;
import br.com.CurriculoAi.models.Role;

import java.util.Set;

public record UserResponse(
        Long userId,
        String name,
        String email,
        Set<RoleResponse> roles
){
}
