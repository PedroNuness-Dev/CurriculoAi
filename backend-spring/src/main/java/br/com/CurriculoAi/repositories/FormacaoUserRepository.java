package br.com.CurriculoAi.repositories;

import br.com.CurriculoAi.models.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormacaoUserRepository extends JpaRepository<Formation, Long> {
}
