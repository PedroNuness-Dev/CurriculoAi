package br.com.CurriculoAi.services.course;

import br.com.CurriculoAi.dto.course.CourseRequest;
import br.com.CurriculoAi.dto.course.CourseResponse;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.course.CourseMapper;
import br.com.CurriculoAi.models.Course;
import br.com.CurriculoAi.repositories.course.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Transactional
    public CourseResponse createCourse(CourseRequest courseRequest) {

        log.info("Iniciando o processo de criação de curso");

        if (courseRepository.existsByNameIgnoreCase(courseRequest.name())) {
            throw new IllegalArgumentException("Já existe um curso com esse nome");
        }

        Course newCourse = Course.builder()
                .name(courseRequest.name())
                .build();

        Course savedCourse = courseRepository.save(newCourse);

        log.info("Curso criado com sucesso! Id: {}", savedCourse.getCourseId());

        return courseMapper.toDto(savedCourse);
    }

    public List<CourseResponse> findAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }

    public CourseResponse getCourseById(Long courseId) {
        return courseMapper.toDto(findCourseById(courseId));
    }

    @Transactional
    public CourseResponse updateCourse(Long courseId, CourseRequest courseRequest) {

        log.info("Iniciando a atualização do curso com id [{}]", courseId);

        Course course = findCourseById(courseId);

        if (courseRepository.existsByNameIgnoreCaseAndCourseIdNot(courseRequest.name(), courseId)) {
            throw new IllegalArgumentException("Já existe um curso com esse nome");
        }

        course.setName(courseRequest.name());

        Course updatedCourse = courseRepository.save(course);

        log.info("Curso atualizado com sucesso! Id: {}", updatedCourse.getCourseId());

        return courseMapper.toDto(updatedCourse);
    }

    // Público para ser reutilizado por outros services
    public Course findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso com id: " + courseId + " não encontrado"));
    }
}