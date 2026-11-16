package br.com.CurriculoAi.controllers.certification;

import br.com.CurriculoAi.dto.certification.CertificationRequest;
import br.com.CurriculoAi.dto.certification.CertificationResponse;
import br.com.CurriculoAi.services.certification.CertificationService;
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
@RequestMapping("/api/certifications")
@Tag(name = "Certificação Controller", description = "Responsável por controlar as ações relacionadas as Certificações do usuário")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @Operation(summary = "Resgistrar certificações do usuário")
    @PostMapping
    public ResponseEntity<List<CertificationResponse>> registerCertifications(
            @RequestBody  List<@Valid CertificationRequest> certificationRequests){

        List<CertificationResponse> certificationResponses = certificationService.registerCertifications(certificationRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(certificationResponses);
    }

    @Operation(summary = "Listar as certificações do usuário autenticado")
    @GetMapping
    public ResponseEntity<List<CertificationResponse>> findAllCertificationsOfUser(){

        List<CertificationResponse> certificationResponses = certificationService.findAllCertificationsOfUser();

        return ResponseEntity.ok(certificationResponses);
    }

    @Operation(summary = "Buscar uma certificação do usuário autenticado pelo id")
    @GetMapping("/{certificationId}")
    public ResponseEntity<CertificationResponse> getCertificationById(@PathVariable Long certificationId){

        CertificationResponse certificationResponse = certificationService.getCertificationById(certificationId);

        return ResponseEntity.ok(certificationResponse);
    }

    @Operation(summary = "Atualizar uma certificação do usuário autenticado")
    @PutMapping("/{certificationId}")
    public ResponseEntity<CertificationResponse> updateCertification(
            @PathVariable Long certificationId,
            @RequestBody @Valid CertificationRequest certificationRequest){

        CertificationResponse certificationResponse = certificationService.updateCertification(certificationId, certificationRequest);

        return ResponseEntity.ok(certificationResponse);
    }

    @Operation(summary = "Remover uma certificação do usuário autenticado")
    @DeleteMapping("/{certificationId}")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long certificationId){

        certificationService.deleteCertification(certificationId);

        return ResponseEntity.noContent().build();
    }
}
