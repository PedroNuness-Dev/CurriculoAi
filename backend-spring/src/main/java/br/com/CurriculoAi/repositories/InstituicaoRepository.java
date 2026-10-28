package br.com.CurriculoAi.repositories;

import br.com.CurriculoAi.models.Instituicao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {
}
