package br.com.CurriculoAi.controllers.course;

import br.com.CurriculoAi.dto.course.CourseRequest;
import br.com.CurriculoAi.dto.course.CourseResponse;
import br.com.CurriculoAi.services.course.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static br.com.CurriculoAi.config.OpenApiResponseRefs.*;

@RestController
@Tag(name = "Catálogo - Cursos", description = "Cursos disponíveis para vincular às formações acadêmicas. Leitura liberada para usuários autenticados; escrita restrita ao perfil ADMIN.")
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(
            summary = "Cadastrar um curso",
            description = "Cadastra um novo curso no catálogo da aplicação. Requer o perfil ADMIN. Retorna 400 se já existir um curso com o mesmo nome."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Curso cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest courseRequest) {

        CourseResponse courseResponse = courseService.createCourse(courseRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(courseResponse);
    }

    @Operation(
            summary = "Listar cursos",
            description = "Retorna todos os cursos cadastrados no catálogo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de cursos retornada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<CourseResponse>> findAllCourses() {

        List<CourseResponse> courses = courseService.findAllCourses();

        return ResponseEntity.ok(courses);
    }

    @Operation(
            summary = "Buscar curso por id",
            description = "Retorna o curso correspondente ao id informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso encontrado"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(@Parameter(description = "Id do curso") @PathVariable Long courseId) {

        CourseResponse courseResponse = courseService.getCourseById(courseId);

        return ResponseEntity.ok(courseResponse);
    }

    @Operation(
            summary = "Atualizar um curso",
            description = "Atualiza os dados de um curso do catálogo. Requer o perfil ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(@Parameter(description = "Id do curso") @PathVariable Long courseId,
                                                       @Valid @RequestBody CourseRequest courseRequest) {

        CourseResponse courseResponse = courseService.updateCourse(courseId, courseRequest);

        return ResponseEntity.ok(courseResponse);
    }
}