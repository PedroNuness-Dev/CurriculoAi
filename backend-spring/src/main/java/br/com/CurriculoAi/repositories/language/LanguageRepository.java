package br.com.CurriculoAi.repositories.language;

import br.com.CurriculoAi.enums.language.LanguageEnum;
import br.com.CurriculoAi.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    Boolean existsByName(LanguageEnum name);
    Boolean existsByNameAndLanguageIdNot(LanguageEnum name, Long languageId);
}
