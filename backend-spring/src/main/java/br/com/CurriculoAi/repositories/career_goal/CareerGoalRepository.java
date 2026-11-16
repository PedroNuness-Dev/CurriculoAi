package br.com.CurriculoAi.repositories.career_goal;

import br.com.CurriculoAi.models.CareerGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CareerGoalRepository extends JpaRepository<CareerGoal, Long> {

    Optional<CareerGoal> findByUser_UserId(Long userId);

    boolean existsByUser_UserId(Long userId);
}
