package br.com.CurriculoAi.repositories.certification;

import br.com.CurriculoAi.models.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    List<Certification> findAllByUser_UserIdOrderByCertificationIdAsc(Long userId);

    Optional<Certification> findByCertificationIdAndUser_UserId(Long certificationId, Long userId);
}
