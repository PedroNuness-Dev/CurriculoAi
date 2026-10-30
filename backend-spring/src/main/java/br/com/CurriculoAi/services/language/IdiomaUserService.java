package br.com.CurriculoAi.services.language;

import br.com.CurriculoAi.dto.request.IdiomaUserDtoRequest;
import br.com.CurriculoAi.dto.response.UsuarioFullContentDtoResponse;
import br.com.CurriculoAi.models.*;
import br.com.CurriculoAi.enums.language.LanguageLevel;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.UsuarioMapper;
import br.com.CurriculoAi.repositories.language.LanguageRepository;
import br.com.CurriculoAi.repositories.user.UserLanguageRepository;
import br.com.CurriculoAi.repositories.TokenIdentificacaoUsurioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IdiomaUserService {

    private final UserLanguageRepository userLanguageRepository;
    private final LanguageRepository languageRepository;
    private final TokenIdentificacaoUsurioRepository tokenIdentificacaoUsurioRepository;
    private final UsuarioMapper usuarioMapper;

    private static final Logger logger = LoggerFactory.getLogger(IdiomaUserService.class);

    @Transactional
    public UsuarioFullContentDtoResponse registerIdiomasUser(List<IdiomaUserDtoRequest> idiomas, String token){

        logger.info("Iniando cadastro de idiomas do usuário, com um total de: {}",idiomas.size());

        TokenIdentificacaoUsuario tokenIdentificacaoUsuario = tokenIdentificacaoUsurioRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token não encontrado"));

        User user = tokenIdentificacaoUsuario.getUsuario();

        List<Long> idsIdiomas = idiomas
                .stream()
                .map(IdiomaUserDtoRequest::id_idioma)
                .distinct()
                .toList();

        Map<Long, Language> idiomasPorId = languageRepository.findAllById(idsIdiomas)
                .stream()
                .collect(Collectors.toMap(Language::getId, Function.identity()));

        List<UserLanguage> registerIdiomas = idiomas.stream()
                .map(idioma -> {

                    Language languageBuscado = Optional.ofNullable(idiomasPorId.get(idioma.id_idioma()))
                            .orElseThrow(() -> new ResourceNotFoundException("Idioma com id: "+ idioma.id_idioma()+" não encontrado"));

                    return new UserLanguage(null, LanguageLevel.from(idioma.nivel()), user, languageBuscado);
                }).toList();

        userLanguageRepository.saveAll(registerIdiomas);

        logger.info("Cadastro de idiomas realizado com sucesso!");

        return usuarioMapper.toFullContentDto(user);
    }
 }
