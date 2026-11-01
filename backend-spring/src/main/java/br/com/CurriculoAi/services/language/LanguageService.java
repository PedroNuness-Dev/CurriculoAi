package br.com.CurriculoAi.services.language;

import br.com.CurriculoAi.dto.language.LanguageRequest;
import br.com.CurriculoAi.dto.language.LanguageResponse;
import br.com.CurriculoAi.enums.language.LanguageEnum;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.language.LanguageMapper;
import br.com.CurriculoAi.models.Language;
import br.com.CurriculoAi.repositories.language.LanguageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class LanguageService {

    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;

    @Transactional
    public LanguageResponse createLanguage(LanguageRequest languageRequest) {

        log.info("Iniciando o processo de criação de idioma");

        if (languageRepository.existsByNameIgnoreCase(languageRequest.name())) {
            throw new IllegalArgumentException("Já existe um idioma com esse nome");
        }

        Language newLanguage = Language.builder()
                .name(LanguageEnum.from(languageRequest.name()))
                .build();

        Language savedLanguage = languageRepository.save(newLanguage);

        log.info("Idioma criado com sucesso! Id: {}", savedLanguage.getLanguageId());

        return languageMapper.toDto(savedLanguage);
    }

    public List<LanguageResponse> findAllLanguages() {

        log.info("Iniciando busca de todos os idiomas");

        List<Language> languages = languageRepository.findAll();

        log.info("Idiomas buscados, com um total de: {}", languages.size());

        return languages
                .stream()
                .map(languageMapper::toDto)
                .toList();
    }

    public LanguageResponse getLanguageById(Long languageId) {
        return languageMapper.toDto(findLanguageById(languageId));
    }

    @Transactional
    public LanguageResponse updateLanguage(Long languageId, LanguageRequest languageRequest) {

        log.info("Iniciando a atualização do idioma com id [{}]", languageId);

        Language language = findLanguageById(languageId);

        if (languageRepository.existsByNameIgnoreCaseAndLanguageIdNot(languageRequest.name(), languageId)) {
            throw new IllegalArgumentException("Já existe um idioma com esse nome");
        }

        language.setName(LanguageEnum.from(languageRequest.name()));

        Language updatedLanguage = languageRepository.save(language);

        log.info("Idioma atualizado com sucesso! Id: {}", updatedLanguage.getLanguageId());

        return languageMapper.toDto(updatedLanguage);
    }

    // Público para ser reutilizado por outros services (ex: UserLanguageService)
    public Language findLanguageById(Long languageId) {
        return languageRepository.findById(languageId)
                .orElseThrow(() -> new ResourceNotFoundException("Idioma com id: " + languageId + " não encontrado"));
    }

    public Map<Long,Language> findLanguagesByIds(Set<Long> languagesIds){
        return languageRepository.findAllById(languagesIds)
                .stream()
                .collect(Collectors.toMap(Language::getLanguageId, Function.identity()));
    }
}