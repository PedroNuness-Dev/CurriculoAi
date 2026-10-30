package br.com.CurriculoAi.services.project;

import br.com.CurriculoAi.dto.request.ProjetoDtoRequest;
import br.com.CurriculoAi.dto.response.ProjetoDtoResponse;
import br.com.CurriculoAi.models.Project;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.project.ProjectRepository;
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

    private final ProjectRepository projectRepository;
    private final UsuarioUtils usuarioUtils;

    private static final Logger logger = LoggerFactory.getLogger(ProjetoService.class);

    @Transactional
    public List<ProjetoDtoResponse> registerProjects(List<ProjetoDtoRequest> projetosParaCadastro){

        if (projetosParaCadastro.isEmpty()) throw new IllegalArgumentException("Lista de projetos está vaiza");

        logger.info("Inicinado cadastro de projetos do usuário");

        User user = usuarioUtils.me();

        List<Project> projetosParaSalvamento = projetosParaCadastro
                .stream()
                .map(projeto -> {
                    return new Project(null, projeto.titulo(),projeto.descriacao(),projeto.link(), user);
                })
                .toList();

        logger.warn("Salvando no banco de dados um total de: {}", projetosParaSalvamento.size());

        projectRepository.saveAll(projetosParaSalvamento);

        logger.info("Cadastro de novos projetos realizado com sucesso");

        return projetosParaSalvamento
                .stream()
                .map(projeto -> {
                    return new ProjetoDtoResponse(projeto.getId(), projeto.getTitulo(), projeto.getDescricao(), projeto.getLink());
                })
                .toList();
    }
}
