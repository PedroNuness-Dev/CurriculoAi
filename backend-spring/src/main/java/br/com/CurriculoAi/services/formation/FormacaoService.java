package br.com.CurriculoAi.services.formation;

import br.com.CurriculoAi.dto.request.FormacaoDtoRequest;
import br.com.CurriculoAi.dto.response.UsuarioTokenIdentResponseDto;
import br.com.CurriculoAi.models.*;
import br.com.CurriculoAi.enums.formation.FormationType;
import br.com.CurriculoAi.exceptions.ListIsEmptyException;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.repositories.CursoRepository;
import br.com.CurriculoAi.repositories.FormacaoUserRepository;
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
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FormacaoService {

    private final FormacaoUserRepository formacaoUserRepository;
    private final TokenIdentificacaoUsurioRepository tokenIdentificacaoUsurioRepository;
    private final CursoRepository cursoRepository;

    private static final Logger logger = LoggerFactory.getLogger(FormacaoService.class);

    //Passo a lista contida no DTO do curso es o dados do método que está chamando
    @Transactional
    public UsuarioTokenIdentResponseDto addFormacaoUser(List<FormacaoDtoRequest> formacoes, String tokenIdentificacao){

        logger.info("Iniciando cadastro de formações do usuário, com o total de: {}", formacoes.size());

        if (formacoes.isEmpty()) throw new ListIsEmptyException("Lista de forações está vazia");

        TokenIdentificacaoUsuario token = tokenIdentificacaoUsurioRepository.findByToken(tokenIdentificacao)
                .orElseThrow(() -> new ResourceNotFoundException("Token de identificação não encontrado"));

        //Pega o usuário contido no token
        User user = token.getUsuario();

        // Pegas todos os ids das áreas sem repetir
        List<Long> idsCurso = formacoes
                .stream()
                .map(FormacaoDtoRequest::id_curso)
                .distinct()
                .toList();

        //Busca todas os cursos no banco em mantém em 'cache'
        Map<Long, Course> cursosPorId = cursoRepository
                .findAllById(idsCurso)
                .stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));

        List<Formation> formacoesSalvas = formacoes
                .stream()
                .map(formacao -> {

                    //Busca no cache os cursos do usuário, pode vir null ai por isso o Optional
                    Course course = Optional.ofNullable(cursosPorId.get(formacao.id_curso()))
                            .orElseThrow(() -> new ResourceNotFoundException("Curso com id: "+formacao.id_curso()+" não encontrado"));

                    return new Formation(
                            null,
                            course,
                            FormationType.from(formacao.tipoFormacao()),
                            formacao.dataInicio(),
                            formacao.dataConclusao(),
                            formacao.emAndamento(),
                            user
                    );
                })
                .toList();

        formacaoUserRepository.saveAll(formacoesSalvas);

        logger.info("Cadastro de formações realizada com sucesso!");

        return new UsuarioTokenIdentResponseDto(token.getId(), user.getNome(), token.getToken());
    }
}
