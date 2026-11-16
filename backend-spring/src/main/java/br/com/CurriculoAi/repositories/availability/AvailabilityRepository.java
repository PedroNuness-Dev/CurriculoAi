package br.com.CurriculoAi.repositories.availability;

import br.com.CurriculoAi.models.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    Optional<Availability> findByUser_UserId(Long userId);

    boolean existsByUser_UserId(Long userId);
}
