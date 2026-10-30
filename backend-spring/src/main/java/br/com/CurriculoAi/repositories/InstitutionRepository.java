package br.com.CurriculoAi.repositories;

import br.com.CurriculoAi.models.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {
}
