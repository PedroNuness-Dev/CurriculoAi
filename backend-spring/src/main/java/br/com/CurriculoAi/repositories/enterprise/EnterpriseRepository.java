package br.com.CurriculoAi.repositories.enterprise;

import br.com.CurriculoAi.models.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {

    Boolean existsByNameIgnoreCase(String name);
    Boolean existsByNameIgnoreCaseAndEnterpriseIdNot(String name, Long enterpriseId);
}
