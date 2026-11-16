package br.com.CurriculoAi.controllers.user;

import br.com.CurriculoAi.dto.user.UserAreaResponse;
import br.com.CurriculoAi.services.user.UserAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/area")
@Tag(name = "User Area Controller")
@RequiredArgsConstructor
public class UserAreaController {

    private final UserAreaService userAreaService;

    @Operation(summary = "Vincular uma área ao usuário autenticado")
    @PostMapping("/{areaId}")
    public ResponseEntity<UserAreaResponse> addAreaToUser(@PathVariable Long areaId){

        UserAreaResponse userAreaResponse = userAreaService.addAreaToUser(areaId);

        return ResponseEntity.ok(userAreaResponse);
    }

    @Operation(summary = "Buscar a área do usuário autenticado")
    @GetMapping
    public ResponseEntity<UserAreaResponse> getAreaOfUser(){

        UserAreaResponse userAreaResponse = userAreaService.getAreaOfUser();

        return ResponseEntity.ok(userAreaResponse);
    }

    @Operation(summary = "Trocar a área do usuário autenticado")
    @PutMapping("/{areaId}")
    public ResponseEntity<UserAreaResponse> updateAreaOfUser(@PathVariable Long areaId){

        UserAreaResponse userAreaResponse = userAreaService.updateAreaOfUser(areaId);

        return ResponseEntity.ok(userAreaResponse);
    }

    @Operation(summary = "Remover a área do usuário autenticado")
    @DeleteMapping
    public ResponseEntity<Void> removeAreaOfUser(){

        userAreaService.removeAreaOfUser();

        return ResponseEntity.noContent().build();
    }
}
