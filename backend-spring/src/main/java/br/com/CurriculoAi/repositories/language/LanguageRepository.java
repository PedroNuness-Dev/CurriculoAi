package br.com.CurriculoAi.repositories.language;

import br.com.CurriculoAi.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    Boolean existsByNameIgnoreCase(String name);
    Boolean existsByNameIgnoreCaseAndLanguageIdNot(String name, Long languageId);
}
