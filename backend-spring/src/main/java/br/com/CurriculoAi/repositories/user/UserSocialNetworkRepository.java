package br.com.CurriculoAi.repositories.user;

import br.com.CurriculoAi.models.UserSocialNetwork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSocialNetworkRepository extends JpaRepository<UserSocialNetwork, Long> {
}
