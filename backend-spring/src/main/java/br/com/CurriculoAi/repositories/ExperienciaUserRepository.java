package br.com.CurriculoAi.repositories;

import br.com.CurriculoAi.models.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperienciaUserRepository extends JpaRepository<Experience, Long>{
}
