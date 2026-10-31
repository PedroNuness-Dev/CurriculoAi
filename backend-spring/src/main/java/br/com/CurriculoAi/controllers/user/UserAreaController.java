package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserAreaResponse;
import br.com.CurriculoAi.services.user.UserAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/area")
@RequiredArgsConstructor
public class UserAreaController {

    private final UserAreaService userAreaService;

    @PostMapping("/{areaId}")
    public ResponseEntity<UserAreaResponse> addAreaToUser(@PathVariable Long areaId){

        UserAreaResponse userAreaResponse = userAreaService.addAreaToUser(areaId);

        return ResponseEntity.ok(userAreaResponse);
    }
}
