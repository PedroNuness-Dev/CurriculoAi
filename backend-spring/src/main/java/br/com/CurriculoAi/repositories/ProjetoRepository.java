package br.com.CurriculoAi.repositories;

import br.com.CurriculoAi.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjetoRepository extends JpaRepository<Project, Long> {
}
