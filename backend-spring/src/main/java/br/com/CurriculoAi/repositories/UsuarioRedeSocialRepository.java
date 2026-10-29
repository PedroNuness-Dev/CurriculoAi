package br.com.CurriculoAi.repositories;

import br.com.CurriculoAi.models.UserSocialNetwork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRedeSocialRepository extends JpaRepository<UserSocialNetwork, Long> {
}
