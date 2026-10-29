package br.com.CurriculoAi.services.social_network;

import br.com.CurriculoAi.dto.request.RedeSocialUsuarioDtoRequest;
import br.com.CurriculoAi.dto.response.UsuarioRedeSocialDtoResponse;
import br.com.CurriculoAi.models.SocialNetwork;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.models.UserSocialNetwork;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.RedeSocialMapper;
import br.com.CurriculoAi.mapper.UsuarioMapper;
import br.com.CurriculoAi.repositories.RedeSocialRepository;
import br.com.CurriculoAi.repositories.UsuarioRedeSocialRepository;
import br.com.CurriculoAi.utils.services.UsuarioUtils;
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
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedeSocialUsuarioService {

    private final RedeSocialRepository redeSocialRepository;
    private final UsuarioUtils usuarioUtils;
    private final UsuarioRedeSocialRepository usuarioRedeSocialRepository;
    private final RedeSocialMapper redeSocialMapper;
    private final UsuarioMapper usuarioMapper;

    private static final Logger logger = LoggerFactory.getLogger(RedeSocialUsuarioService.class);

    @Transactional
    public List<UsuarioRedeSocialDtoResponse> registerRedeSocialUsuario(List<RedeSocialUsuarioDtoRequest> redesSociais){

        logger.info("Iniciando processo de registro de idiomas ao usuário");

        User userAutenticado = usuarioUtils.me();

        List<Long> idsRedeSociais = redesSociais
                .stream()
                .map(RedeSocialUsuarioDtoRequest::id_rede_social)
                .distinct()
                .toList();

        Map<Long, SocialNetwork> redesSociaisBuscadas = redeSocialRepository.findAllById(idsRedeSociais)
                .stream()
                .collect(Collectors.toMap(SocialNetwork::getId, Function.identity()));


        List<UserSocialNetwork> redesSociaisUsuarioParaSalvar = redesSociais
                .stream()
                .map(redeSocial -> {

                    SocialNetwork socialNetworkBuscada = Optional.ofNullable(redesSociaisBuscadas.get(redeSocial.id_rede_social()))
                            .orElseThrow(() -> new ResourceNotFoundException("Rede social com ID: "+redeSocial.id_rede_social()));

                    return new UserSocialNetwork(null, redeSocial.link(), userAutenticado, socialNetworkBuscada);
                })
                .toList();

        logger.info("Registro de redes sociais feito com sucesso, com uma quantidade de: {}", redesSociaisUsuarioParaSalvar.size());

        return usuarioRedeSocialRepository.saveAll(redesSociaisUsuarioParaSalvar)
                .stream()
                .map(userSocialNetwork -> {
                    return new UsuarioRedeSocialDtoResponse(
                            userSocialNetwork.getId(),
                            userSocialNetwork.getLink(),
                            redeSocialMapper.toDto(userSocialNetwork.getSocialNetwork())
                    );
                })
                .toList();
    }
}
