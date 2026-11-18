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

    @Transactional
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

    public List<UserLanguageResponse> getUserLanguagesOfUser(){

        User user = authService.getUserByTokenAuthenticate();

        List<UserLanguage> userLanguages = userLanguageRepository.findAllUserLanguageOfUser(user);

        return userLanguages
                .stream()
                .map(userLanguageMapper::toDto)
                .toList();
    }

    public UserLanguageResponse getUserLanguageById(Long userLanguageId){

        UserLanguage userLanguage = findUserLanguageOfAuthenticatedUser(userLanguageId);

        return userLanguageMapper.toDto(userLanguage);
    }

    @Transactional
    public UserLanguageResponse updateUserLanguage(Long userLanguageId, UserLanguageRequest userLanguageRequest){

        log.info("Iniciando atualização do idioma do usuário com id [{}]", userLanguageId);

        UserLanguage userLanguage = findUserLanguageOfAuthenticatedUser(userLanguageId);

        Language language = languageService.findLanguageById(userLanguageRequest.languageId());

        userLanguage.setLanguage(language);
        userLanguage.setLevel(LanguageLevel.from(userLanguageRequest.languageLevel()));

        UserLanguage updatedUserLanguage = userLanguageRepository.save(userLanguage);

        log.info("Idioma do usuário com id [{}] atualizado com sucesso", userLanguageId);

        return userLanguageMapper.toDto(updatedUserLanguage);
    }

    @Transactional
    public void deleteUserLanguage(Long userLanguageId){

        log.info("Iniciando remoção do idioma do usuário com id [{}]", userLanguageId);

        UserLanguage userLanguage = findUserLanguageOfAuthenticatedUser(userLanguageId);

        userLanguageRepository.delete(userLanguage);

        log.info("Idioma do usuário com id [{}] removido com sucesso", userLanguageId);
    }

    // Busca já filtrando pelo usuário autenticado: se o idioma for de outro usuário, retorna 404
    private UserLanguage findUserLanguageOfAuthenticatedUser(Long userLanguageId){

        Long userId = authService.getUserIdByTokenAuthenticate();

        return userLanguageRepository.findByUserLanguageIdAndUser_UserId(userLanguageId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Idioma do usuário com id: "+userLanguageId+" não encontrado"));
    }

    private Map<Long, Language> getLanguagesByUserLanguagesRequest(List<UserLanguageRequest> userLanguageRequests){
        Set<Long> languagesIds = userLanguageRequests
                .stream()
                .map(UserLanguageRequest::languageId)
                .collect(Collectors.toSet());

        return languageService.findLanguagesByIds(languagesIds);
    }
}
