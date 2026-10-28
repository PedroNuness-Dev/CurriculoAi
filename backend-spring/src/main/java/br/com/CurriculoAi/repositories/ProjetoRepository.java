package br.com.CurriculoAi.repositories;

import br.com.CurriculoAi.models.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
}
