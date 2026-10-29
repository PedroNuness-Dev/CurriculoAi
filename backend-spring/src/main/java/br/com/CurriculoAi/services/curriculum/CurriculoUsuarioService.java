package br.com.CurriculoAi.services.curriculum;

import br.com.CurriculoAi.models.UserCurriculum;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.CurriculoUsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CurriculoUsuarioService {

    private static final int LIMITE_CURRICULOS = 3;

    private final Logger logger = Logger.getLogger(CurriculoUsuarioService.class.getName());

    private final CurriculoUsuarioRepository curriculoUsuarioRepository;

    public CurriculoUsuarioService(CurriculoUsuarioRepository curriculoUsuarioRepository) {
        this.curriculoUsuarioRepository = curriculoUsuarioRepository;
    }

    public void salvar(byte[] pdf, User user) {

        List<UserCurriculum> curriculosExistentes = curriculoUsuarioRepository
                .findByUsuario(user);

        if (curriculosExistentes.size() >= LIMITE_CURRICULOS) {

            UserCurriculum maisAntigo = curriculosExistentes
                    .stream()
                    .min(Comparator.comparing(UserCurriculum::getDataGeracao))
                    .orElseThrow();

            logger.info("Limite atingido. Removendo currículo mais antigo: id=" + maisAntigo.getId_curriculo());

            curriculoUsuarioRepository.delete(maisAntigo);
        }

        UserCurriculum novo = UserCurriculum.builder()
                .curriculo(pdf)
                .dataGeracao(LocalDateTime.now())
                .user(user)
                .build();

        curriculoUsuarioRepository.save(novo);

        logger.info("Currículo salvo com sucesso para o usuário: " + user.getId());
    }
}