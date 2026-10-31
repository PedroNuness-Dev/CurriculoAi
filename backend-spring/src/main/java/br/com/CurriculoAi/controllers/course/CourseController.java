package br.com.CurriculoAi.controllers.course;

import br.com.CurriculoAi.dto.course.CourseRequest;
import br.com.CurriculoAi.dto.course.CourseResponse;
import br.com.CurriculoAi.services.course.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Course Controller", description = "Responsável por controlar as ações relacionadas aos cursos")
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Cadastrar um curso")
    @PostMapping("/create")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest courseRequest) {

        CourseResponse courseResponse = courseService.createCourse(courseRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(courseResponse);
    }

    @Operation(summary = "Listar todos os cursos")
    @GetMapping
    public ResponseEntity<List<CourseResponse>> findAllCourses() {

        List<CourseResponse> courses = courseService.findAllCourses();

        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Buscar um curso pelo id")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long courseId) {

        CourseResponse courseResponse = courseService.getCourseById(courseId);

        return ResponseEntity.ok(courseResponse);
    }

    @Operation(summary = "Atualizar um curso")
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long courseId,
                                                       @Valid @RequestBody CourseRequest courseRequest) {

        CourseResponse courseResponse = courseService.updateCourse(courseId, courseRequest);

        return ResponseEntity.ok(courseResponse);
    }
}