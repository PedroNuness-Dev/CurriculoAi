package br.com.CurriculoAi.services.user;

import br.com.CurriculoAi.dto.user.UserLanguageRequest;
import br.com.CurriculoAi.dto.user.UserLanguageResponse;
import br.com.CurriculoAi.enums.language.LanguageLevel;
import br.com.CurriculoAi.exceptions.ListIsEmptyException;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.user.UserLanguageMapper;
import br.com.CurriculoAi.models.Language;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.models.UserLanguage;
import br.com.CurriculoAi.repositories.user.UserLanguageRepository;
import br.com.CurriculoAi.services.auth.AuthService;
import br.com.CurriculoAi.services.language.LanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserLanguageService {

    private final UserLanguageRepository userLanguageRepository;
    private final AuthService authService;
    private final LanguageService languageService;
    private final UserLanguageMapper userLanguageMapper;

    public List<UserLanguageResponse> registerLanguages(List<UserLanguageRequest> userLanguageRequests){

        if (userLanguageRequests.isEmpty()) throw new ListIsEmptyException("Lista de idiomas para atribuir ao usuário não pode ser vazia");

        User user = authService.getUserByTokenAuthenticate();

        log.info("Iniciando registro de idiomas ao usuário [{}], com um total de: [{}]", user.getEmail(), userLanguageRequests.size());

        Map<Long,Language> languagesMap = getLanguagesByUserLanguagesRequest(userLanguageRequests);

        List<UserLanguage> userLanguagesForSave = userLanguageRequests
                .stream()
                .map(userLanguageRequest -> {
                    Language language = Optional.ofNullable(languagesMap.get(userLanguageRequest.languageId()))
                            .orElseThrow(() -> new ResourceNotFoundException("Idioma com id"+ userLanguageRequest.languageId()+" não encontrado"));

                    return UserLanguage.builder()
                            .language(language)
                            .user(user)
                            .level(LanguageLevel.from(userLanguageRequest.languageLevel()))
                            .build();
                })
                .toList();

        List<UserLanguage> userLanguagesSave = userLanguageRepository.saveAll(userLanguagesForSave);

        return userLanguagesSave
                .stream()
                .map(userLanguageMapper::toDto)
                .toList();
    }

    private Map<Long, Language> getLanguagesByUserLanguagesRequest(List<UserLanguageRequest> userLanguageRequests){
        Set<Long> languagesIds = userLanguageRequests
                .stream()
                .map(UserLanguageRequest::languageId)
                .collect(Collectors.toSet());

        return languageService.findLanguagesByIds(languagesIds);
    }
}
