package br.com.CurriculoAi.controllers.experience;

import br.com.CurriculoAi.dto.experience.ExperienceRequest;
import br.com.CurriculoAi.dto.experience.ExperienceResponse;
import br.com.CurriculoAi.services.experience.ExperienciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/experiences")
@Tag(name = "Experiência Controller", description = "Responsável por controlar as ações relacionadas as experiências do usuário")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienciaService experienciaService;

    @Operation(summary = "Resgistrar as experiências do usuário")
    @PostMapping
    public ResponseEntity<List<ExperienceResponse>> registerExperiences(@RequestBody List<@Valid ExperienceRequest> experienceRequests){

        List<ExperienceResponse> experienceResponses = experienciaService.registerExperiences(experienceRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(experienceResponses);
    }
}
