package br.com.CurriculoAi.repositories.experience;

import br.com.CurriculoAi.models.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findAllByUser_UserIdOrderByExperienceIdAsc(Long userId);

    Optional<Experience> findByExperienceIdAndUser_UserId(Long experienceId, Long userId);
}
