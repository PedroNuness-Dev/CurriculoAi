package br.com.CurriculoAi.controllers.certification;

import br.com.CurriculoAi.dto.certification.CertificationRequest;
import br.com.CurriculoAi.dto.certification.CertificationResponse;
import br.com.CurriculoAi.services.certification.CertificacaoService;
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
@RequestMapping("/api/certifications")
@Tag(name = "Certificação Controller", description = "Responsável por controlar as ações relacionadas as Certificações do usuário")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificacaoService certificacaoService;

    @Operation(summary = "Resgistrar certificações do usuário")
    @PostMapping
    public ResponseEntity<List<CertificationResponse>> registerCertifications(
            @RequestBody  List<@Valid CertificationRequest> certificationRequests){

        List<CertificationResponse> certificationResponses = certificacaoService.registerCertifications(certificationRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(certificationResponses);
    }
}
