package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserSocialNetworkRequest;
import br.com.CurriculoAi.dto.user.UserSocialNetworkResponse;
import br.com.CurriculoAi.services.user.UserSocialNetworkService;
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
@RequestMapping("/api/users/social-networks")
@Tag(name = "User Social Network Controller")
@RequiredArgsConstructor
public class UserSocialNetworkController {

    private final UserSocialNetworkService userSocialNetworkService;

    @PostMapping
    public ResponseEntity<List<UserSocialNetworkResponse>> registerUserSocialNetworks(
            @RequestBody List<@Valid UserSocialNetworkRequest> userSocialNetworkRequests){

        List<UserSocialNetworkResponse> userSocialNetworkResponses = userSocialNetworkService.registerUserSocialNetworks(userSocialNetworkRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(userSocialNetworkResponses);
    }
}
