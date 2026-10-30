package br.com.CurriculoAi.dto.user;

import br.com.CurriculoAi.models.Role;

import java.util.Set;

public record UserResponse(
        Long id,
        String nome,
        String email,
        Set<Role> roles
){
}
