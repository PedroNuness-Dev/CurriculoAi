package br.com.CurriculoAi.controllers.github;

import br.com.CurriculoAi.dto.github.GitHubImportRequest;
import br.com.CurriculoAi.dto.github.GitHubPreview;
import br.com.CurriculoAi.dto.project.ProjectResponse;
import br.com.CurriculoAi.services.github.GitHubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/github")
@PreAuthorize("hasRole('CANDIDATO')")
@RequiredArgsConstructor
public class GitHubController {
    private final GitHubService service;

    @GetMapping("/preview")
    public ResponseEntity<GitHubPreview> preview(@RequestParam String username,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(service.preview(username, page));
    }

    @PostMapping("/import")
    public ResponseEntity<List<ProjectResponse>> importProjects(@Valid @RequestBody GitHubImportRequest request) {
        return ResponseEntity.ok(service.importProjects(request));
    }
}
