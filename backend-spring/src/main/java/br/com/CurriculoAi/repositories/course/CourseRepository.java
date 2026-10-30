package br.com.CurriculoAi.repositories.course;

import br.com.CurriculoAi.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
