package br.com.CurriculoAi.repositories.user;

import br.com.CurriculoAi.models.UserLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLanguageRepository extends JpaRepository<UserLanguage, Long> {
}
