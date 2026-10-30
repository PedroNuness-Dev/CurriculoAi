package br.com.CurriculoAi.repositories;

import br.com.CurriculoAi.models.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilityRepository extends JpaRepository <Availability, Long>{
}
