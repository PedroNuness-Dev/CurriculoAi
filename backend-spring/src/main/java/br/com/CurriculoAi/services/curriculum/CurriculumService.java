package br.com.CurriculoAi.services.curriculum;

import br.com.CurriculoAi.dto.curriculum.CurriculumRequest;
import br.com.CurriculoAi.dto.curriculum.CurriculumResponse;
import br.com.CurriculoAi.mapper.curriculum.CurriculumMapper;
import br.com.CurriculoAi.models.Curriculum;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.curriculum.CurriculumRepository;
import br.com.CurriculoAi.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CurriculumService {

    private static final int LIMIT_CURRICULUM = 3;

    private final CurriculumRepository curriculumRepository;
    private final AuthService authService;
    private final CurriculumMapper curriculumMapper;

    public CurriculumResponse registerCurriculum(CurriculumRequest curriculumRequest) {

        User user = authService.getUserByTokenAuthenticate();

        Long amountCurriculumUser = curriculumRepository.amountCurriculumByUser(user.getUserId());

        if (amountCurriculumUser >= LIMIT_CURRICULUM) throw new IllegalArgumentException(
                "Limite de curriculos salvos atingido, delete pelo menos um para conseguir salvar");


        Curriculum curriculum = Curriculum.builder()
                .title(curriculumRequest.title())
                .curriculum(curriculumRequest.curriculum())
                .user(user)
                .build();

        Curriculum curriculumSaved = curriculumRepository.save(curriculum);

        log.info("Currículo salvo com sucesso para o usuário: [{}]", user.getEmail());

        return curriculumMapper.toDto(curriculumSaved);
    }
}