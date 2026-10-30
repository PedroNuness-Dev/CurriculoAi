package br.com.CurriculoAi.services.course;

import br.com.CurriculoAi.dto.response.CursoDtoResponse;
import br.com.CurriculoAi.models.Course;
import br.com.CurriculoAi.repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CursoService {

    private final CourseRepository courseRepository;

    private static final Logger logger = LoggerFactory.getLogger(CursoService.class);

    public List<CursoDtoResponse> findAll(){

        logger.info("Buscando cursos...");

        List<Course> cursosBuscados = courseRepository.findAll();

        logger.info("Cursos buscados, com um total de: {}", cursosBuscados.size());

        return cursosBuscados
                .stream()
                .map(curso -> {
                    return new CursoDtoResponse(curso.getId(), curso.getNomeCurso());
                })
                .toList();
    }
}
