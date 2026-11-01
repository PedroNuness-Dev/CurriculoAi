package br.com.CurriculoAi.repositories.curriculum;

import br.com.CurriculoAi.models.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

    @Query("""
    SELECT COUNT(c) FROM Curriculum c
    WHERE c.user.userId = :userId
""")
    Long amountCurriculumByUser(@Param("userId") Long userId);
}