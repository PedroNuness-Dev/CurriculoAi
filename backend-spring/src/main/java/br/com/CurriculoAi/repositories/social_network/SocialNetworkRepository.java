package br.com.CurriculoAi.repositories.social_network;

import br.com.CurriculoAi.models.SocialNetwork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialNetworkRepository extends JpaRepository<SocialNetwork, Long> {

    Boolean existsByNameIgnoreCase(String name);
    Boolean existsByNameIgnoreCaseAndSocialNetworkIdNot(String name, Long socialNetworkId);
}
