package br.com.CurriculoAi.repositories;

import br.com.CurriculoAi.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Course, Long> {
}
