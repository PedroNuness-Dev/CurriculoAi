package br.com.CurriculoAi.services.area;

import br.com.CurriculoAi.dto.request.AreaUserAddDtoRequest;
import br.com.CurriculoAi.dto.response.AreaDTOResponse;
import br.com.CurriculoAi.dto.response.UsuarioTokenIdentResponseDto;
import br.com.CurriculoAi.models.Area;
import br.com.CurriculoAi.models.TokenIdentificacaoUsuario;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.repositories.AreaRepository;
import br.com.CurriculoAi.repositories.TokenIdentificacaoUsurioRepository;
import br.com.CurriculoAi.repositories.UserRepository;
import br.com.CurriculoAi.utils.services.UsuarioUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;
    private final TokenIdentificacaoUsurioRepository tokenIdentificacaoUsurioRepository;
    private final UserRepository userRepository;
    private final UsuarioUtils usuarioUtils;

    private final static Logger logger = LoggerFactory.getLogger(AreaService.class);

    @Transactional
    public UsuarioTokenIdentResponseDto addAreaUser(AreaUserAddDtoRequest areaUserAddDtoRequest){

        logger.info("Adicionando área com id: {} ao usuário com token de identificação: {}", areaUserAddDtoRequest.idArea(), areaUserAddDtoRequest.tokenIdentificacaoUsuario());

        //Pego o token do dto
        TokenIdentificacaoUsuario tokenIdentificacao = tokenIdentificacaoUsurioRepository.findByToken(areaUserAddDtoRequest.tokenIdentificacaoUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Token de identificação inválido"));

        //Pego usuário contido no token
        User user = tokenIdentificacao.getUsuario();

        Area areaBuscada = areaRepository.findById(areaUserAddDtoRequest.idArea())
                .orElseThrow(() -> new ResourceNotFoundException("Área com id "+areaUserAddDtoRequest.idArea()+" não encontrada"));

        user.setArea(areaBuscada);

        User userAtualizado = userRepository.save(user);

        logger.info("Areá: {} adicionada com sucesso ao usuário com id: {}", areaBuscada.getNomeArea(), user.getId());

        return new UsuarioTokenIdentResponseDto(tokenIdentificacao.getId(), user.getNome(), tokenIdentificacao.getToken());
    }

    @Transactional
    public AreaDTOResponse updateAreaUser(Long idBuscado){

        logger.info("Iniando processo de atualização da área do usuário");

        Area areaSearched = areaRepository.findById(idBuscado)
                .orElseThrow(() -> new ResourceNotFoundException("Área com id: "+idBuscado+" não encontrada"));

        User user = usuarioUtils.me();

        if (user.getArea().getId() == areaSearched.getId()){
            throw new IllegalArgumentException("Área não pode ser a mesma, é necessário ser uma área diferente para atualizar");
        }

        user.setArea(areaSearched);

        logger.info("Área atualizada com sucesso! Área: {}", areaSearched.getNomeArea());

        User userAtualizado =  userRepository.save(user);

        return new AreaDTOResponse(areaSearched.getId(),areaSearched.getNomeArea());
    }

    public List<AreaDTOResponse> findAll(){

        logger.info("Buscando área...");

        List<Area> areasBuscadas = areaRepository.findAll();

        logger.info("Área buscadas, com um total de: {}", areasBuscadas.size());
        return areasBuscadas
                .stream()
                .map(area -> {
                    return new AreaDTOResponse(area.getId(), area.getNomeArea());
                })
                .toList();
    }
}
