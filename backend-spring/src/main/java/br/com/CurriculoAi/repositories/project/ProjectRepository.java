package br.com.CurriculoAi.repositories.project;

import br.com.CurriculoAi.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByUser_UserIdOrderByProjectIdAsc(Long userId);

    Optional<Project> findByProjectIdAndUser_UserId(Long projectId, Long userId);
}
