package br.com.CurriculoAi.repositories;

import br.com.CurriculoAi.models.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {
}
