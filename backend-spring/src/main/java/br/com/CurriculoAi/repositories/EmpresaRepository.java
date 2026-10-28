package br.com.CurriculoAi.repositories;

import br.com.CurriculoAi.models.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}
