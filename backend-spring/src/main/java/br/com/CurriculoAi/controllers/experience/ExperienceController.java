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
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Listar as experiências do usuário autenticado")
    @GetMapping
    public ResponseEntity<List<ExperienceResponse>> findAllExperiencesOfUser(){

        List<ExperienceResponse> experienceResponses = experienciaService.findAllExperiencesOfUser();

        return ResponseEntity.ok(experienceResponses);
    }

    @Operation(summary = "Buscar uma experiência do usuário autenticado pelo id")
    @GetMapping("/{experienceId}")
    public ResponseEntity<ExperienceResponse> getExperienceById(@PathVariable Long experienceId){

        ExperienceResponse experienceResponse = experienciaService.getExperienceById(experienceId);

        return ResponseEntity.ok(experienceResponse);
    }

    @Operation(summary = "Atualizar uma experiência do usuário autenticado")
    @PutMapping("/{experienceId}")
    public ResponseEntity<ExperienceResponse> updateExperience(
            @PathVariable Long experienceId,
            @RequestBody @Valid ExperienceRequest experienceRequest){

        ExperienceResponse experienceResponse = experienciaService.updateExperience(experienceId, experienceRequest);

        return ResponseEntity.ok(experienceResponse);
    }

    @Operation(summary = "Remover uma experiência do usuário autenticado")
    @DeleteMapping("/{experienceId}")
    public ResponseEntity<Void> deleteExperience(@PathVariable Long experienceId){

        experienciaService.deleteExperience(experienceId);

        return ResponseEntity.noContent().build();
    }
}
