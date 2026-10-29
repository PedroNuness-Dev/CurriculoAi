package br.com.CurriculoAi.services;

import br.com.CurriculoAi.dto.request.ProjetoDtoRequest;
import br.com.CurriculoAi.dto.response.ProjetoDtoResponse;
import br.com.CurriculoAi.models.Projeto;
import br.com.CurriculoAi.models.Usuario;
import br.com.CurriculoAi.repositories.ProjetoRepository;
import br.com.CurriculoAi.utils.services.UsuarioUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final UsuarioUtils usuarioUtils;

    private static final Logger logger = LoggerFactory.getLogger(ProjetoService.class);

    @Transactional
    public List<ProjetoDtoResponse> registerProjects(List<ProjetoDtoRequest> projetosParaCadastro){

        if (projetosParaCadastro.isEmpty()) throw new IllegalArgumentException("Lista de projetos está vaiza");

        logger.info("Inicinado cadastro de projetos do usuário");

        Usuario usuario = usuarioUtils.me();

        List<Projeto> projetosParaSalvamento = projetosParaCadastro
                .stream()
                .map(projeto -> {
                    return new Projeto(null, projeto.titulo(),projeto.descriacao(),projeto.link(), usuario);
                })
                .toList();

        logger.warn("Salvando no banco de dados um total de: {}", projetosParaSalvamento.size());

        projetoRepository.saveAll(projetosParaSalvamento);

        logger.info("Cadastro de novos projetos realizado com sucesso");

        return projetosParaSalvamento
                .stream()
                .map(projeto -> {
                    return new ProjetoDtoResponse(projeto.getId(), projeto.getTitulo(), projeto.getDescricao(), projeto.getLink());
                })
                .toList();
    }
}
