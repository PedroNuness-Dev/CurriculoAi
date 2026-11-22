package br.com.CurriculoAi.services.github;

import br.com.CurriculoAi.client.github.GitHubClient;
import br.com.CurriculoAi.dto.project.ProjectResponse;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GitHubEvidenceServiceTest {
    @Test void ignoresExternalHostsAndDegradesWhenGithubIsUnavailable() {
        var client = mock(GitHubClient.class);
        var service = new GitHubEvidenceService(client);
        assertTrue(service.inspect(List.of(project("http://localhost/private"))).isEmpty());
        verifyNoInteractions(client);
        when(client.repository("dev", "repo")).thenThrow(new RuntimeException("unavailable"));
        var result = service.inspect(List.of(project("https://github.com/dev/repo")));
        assertEquals(1, result.size());
        assertTrue(result.getFirst().get("status").toString().contains("indisponível"));
    }
    @Test void readsOnlyBoundedTextFilesAndKeepsProvenance() {
        var client = mock(GitHubClient.class);
        String sha = "a".repeat(40);
        when(client.repository("dev", "repo")).thenReturn("{\"default_branch\":\"main\"}");
        when(client.tree("dev", "repo", "main")).thenReturn("{\"tree\":[{\"path\":\"src/Main.java\",\"type\":\"blob\",\"size\":12,\"sha\":\"" + sha + "\"}]}");
        when(client.blob("dev", "repo", sha)).thenReturn("{\"encoding\":\"base64\",\"size\":12,\"content\":\"Y2xhc3MgTWFpbiB7fQ==\"}");
        var result = new GitHubEvidenceService(client).inspect(List.of(project("https://github.com/dev/repo")));
        assertTrue(result.getFirst().get("files").toString().contains("class Main {}"));
        assertTrue(result.getFirst().get("files").toString().contains(sha));
    }
    private ProjectResponse project(String link) { return new ProjectResponse(1L, "Projeto", "Descrição", link, Set.of()); }
}
