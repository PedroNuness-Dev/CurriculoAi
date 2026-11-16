package br.com.CurriculoAi.controllers.certification;

import br.com.CurriculoAi.dto.certification.CertificationRequest;
import br.com.CurriculoAi.dto.certification.CertificationResponse;
import br.com.CurriculoAi.services.certification.CertificationService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static br.com.CurriculoAi.config.OpenApiResponseRefs.*;

@Validated
@RestController
@Tag(name = "Candidato - Certificações", description = "Certificações e cursos livres do candidato autenticado")
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CANDIDATO')")
public class CertificationController {

    private final CertificationService certificationService;

    @Operation(
            summary = "Cadastrar certificações",
            description = "Cadastra uma ou mais certificações vinculadas a instituições do catálogo. Sem `finishDate`, a certificação fica como em andamento."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Certificações cadastradas com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PostMapping
    public ResponseEntity<List<CertificationResponse>> registerCertifications(
            @RequestBody  List<@Valid CertificationRequest> certificationRequests){

        List<CertificationResponse> certificationResponses = certificationService.registerCertifications(certificationRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(certificationResponses);
    }

    @Operation(
            summary = "Listar certificações",
            description = "Retorna todas as certificações do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de certificações retornada com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN)
    })
    @GetMapping
    public ResponseEntity<List<CertificationResponse>> findAllCertificationsOfUser(){

        List<CertificationResponse> certificationResponses = certificationService.findAllCertificationsOfUser();

        return ResponseEntity.ok(certificationResponses);
    }

    @Operation(
            summary = "Buscar certificação por id",
            description = "Retorna uma certificação do candidato autenticado. Retorna 404 se o id não existir ou pertencer a outro usuário."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Certificação encontrada"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @GetMapping("/{certificationId}")
    public ResponseEntity<CertificationResponse> getCertificationById(@Parameter(description = "Id da certificação do candidato") @PathVariable Long certificationId){

        CertificationResponse certificationResponse = certificationService.getCertificationById(certificationId);

        return ResponseEntity.ok(certificationResponse);
    }

    @Operation(
            summary = "Atualizar uma certificação",
            description = "Atualiza uma certificação do candidato autenticado. Sem `finishDate`, a certificação passa a ficar em andamento."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Certificação atualizada com sucesso"),
            @ApiResponse(responseCode = "400", ref = BAD_REQUEST),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @PutMapping("/{certificationId}")
    public ResponseEntity<CertificationResponse> updateCertification(
            @Parameter(description = "Id da certificação do candidato")
            @PathVariable Long certificationId,
            @RequestBody @Valid CertificationRequest certificationRequest){

        CertificationResponse certificationResponse = certificationService.updateCertification(certificationId, certificationRequest);

        return ResponseEntity.ok(certificationResponse);
    }

    @Operation(
            summary = "Remover uma certificação",
            description = "Remove uma certificação do candidato autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Certificação removida com sucesso"),
            @ApiResponse(responseCode = "401", ref = UNAUTHORIZED),
            @ApiResponse(responseCode = "403", ref = FORBIDDEN),
            @ApiResponse(responseCode = "404", ref = NOT_FOUND)
    })
    @DeleteMapping("/{certificationId}")
    public ResponseEntity<Void> deleteCertification(@Parameter(description = "Id da certificação do candidato") @PathVariable Long certificationId){

        certificationService.deleteCertification(certificationId);

        return ResponseEntity.noContent().build();
    }
}
