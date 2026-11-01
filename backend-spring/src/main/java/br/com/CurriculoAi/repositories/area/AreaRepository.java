package br.com.CurriculoAi.repositories.area;

import br.com.CurriculoAi.models.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    Boolean existsByNameIgnoreCase(String name);

    Boolean existsByNameIgnoreCaseAndAreaIdNot(String name, Long areaId);
}
