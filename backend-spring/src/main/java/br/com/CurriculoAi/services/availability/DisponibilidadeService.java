package br.com.CurriculoAi.services.availability;

import br.com.CurriculoAi.dto.request.DisponibilidadeDtoRequest;
import br.com.CurriculoAi.dto.response.UsuarioTokenIdentResponseDto;
import br.com.CurriculoAi.models.Availability;
import br.com.CurriculoAi.models.TokenIdentificacaoUsuario;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.enums.availability.WorkModel;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.repositories.DisponibilidadeUserRepository;
import br.com.CurriculoAi.repositories.TokenIdentificacaoUsurioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DisponibilidadeService {

    private final DisponibilidadeUserRepository disponibilidadeUserRepository;
    private final TokenIdentificacaoUsurioRepository tokenIdentificacaoUsurioRepository;

    private static final Logger logger = LoggerFactory.getLogger(DisponibilidadeService.class);

    @Transactional
    public UsuarioTokenIdentResponseDto cadastrarDisponibilidade(DisponibilidadeDtoRequest disponibilidadeDtoRequest, String token){

        logger.info("Iniciando o cadastro de disponibilidade do usuário...");

        TokenIdentificacaoUsuario tokenIdentificacaoUsuario = tokenIdentificacaoUsurioRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token não encontrado"));

        User user = tokenIdentificacaoUsuario.getUsuario();

        Availability novaDisponibilidade = new Availability(
                null,
                disponibilidadeDtoRequest.disponibilidadeInicio(),
                WorkModel.from(disponibilidadeDtoRequest.modeloTrabalho()),
                user);

        Availability availabilitySalva = disponibilidadeUserRepository.save(novaDisponibilidade);

        logger.info("Disponibilidade salva com sucesso! Com id: {}", availabilitySalva.getId());

        return new UsuarioTokenIdentResponseDto(tokenIdentificacaoUsuario.getId(), user.getNome(), tokenIdentificacaoUsuario.getToken());
    }
}
