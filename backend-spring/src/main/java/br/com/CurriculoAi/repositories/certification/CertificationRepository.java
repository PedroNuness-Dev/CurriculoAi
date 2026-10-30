package br.com.CurriculoAi.repositories.certification;

import br.com.CurriculoAi.models.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long>{
}
