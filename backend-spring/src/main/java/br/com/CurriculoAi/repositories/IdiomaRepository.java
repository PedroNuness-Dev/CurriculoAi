package br.com.CurriculoAi.repositories;

import br.com.CurriculoAi.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdiomaRepository extends JpaRepository<Language, Long> {
}
