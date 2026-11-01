package br.com.CurriculoAi.repositories.formation;

import br.com.CurriculoAi.models.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {

    Optional<Formation> findByFormationIdAndUser_UserId(Long formationId, Long userId);

    @Query("""
    SELECT f FROM Formation f
    WHERE f.user.userId = :userid
    ORDER BY f.formationId
""")
    List<Formation> findAllFormationsUser(@Param("userId") Long userId);
}
