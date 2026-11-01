package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserLanguageRequest;
import br.com.CurriculoAi.dto.user.UserLanguageResponse;
import br.com.CurriculoAi.services.user.UserLanguageService;
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
@RequestMapping("/api/users/languages")
@Tag(name = "User Language Controller")
@RequiredArgsConstructor
public class UserLanguageController {

    private final UserLanguageService userLanguageService;

    @PostMapping
    public ResponseEntity<List<UserLanguageResponse>> registerLanguages(@RequestBody List<@Valid UserLanguageRequest> userLanguageRequests){

        List<UserLanguageResponse> userLanguageResponse = userLanguageService.registerLanguages(userLanguageRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(userLanguageResponse);
    }
}
