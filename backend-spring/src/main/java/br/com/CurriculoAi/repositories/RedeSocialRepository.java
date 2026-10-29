package br.com.CurriculoAi.repositories;

import br.com.CurriculoAi.models.SocialNetwork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RedeSocialRepository extends JpaRepository<SocialNetwork, Long> {
}
