package br.com.CurriculoAi.services.github;

import br.com.CurriculoAi.client.github.GitHubClient;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GitHubServiceTest {
    private final GitHubClient client = mock(GitHubClient.class);
    private final GitHubService service = new GitHubService(client, null, null);

    @Test
    void acceptsOnlyPublicGitHubProfileIdentifiers() {
        assertEquals("octocat", GitHubService.username(" https://github.com/octocat/ "));
        assertEquals("octocat", GitHubService.username("octocat"));
        assertThrows(IllegalArgumentException.class, () -> GitHubService.username("https://example.org/octocat"));
        assertThrows(IllegalArgumentException.class, () -> GitHubService.username("https://github.com/a/repo"));
        assertThrows(IllegalArgumentException.class, () -> GitHubService.username("https://github.com@127.0.0.1/test"));
    }

    @Test
    void preservesForkAndLanguageAsMetadata() {
        when(client.profile("octocat")).thenReturn("{\"login\":\"octocat\",\"name\":null}");
        when(client.repositories("octocat", 1)).thenReturn("""
                [{"name":"demo","owner":{"login":"octocat"},"description":null,"language":"Java","fork":true}]
                """);
        var result = service.preview("octocat", 1);
        assertEquals("", result.name());
        assertEquals("https://github.com/octocat/demo", result.repositories().getFirst().url());
        assertTrue(result.repositories().getFirst().fork());
        assertFalse(result.hasMore());
    }

    @Test
    void rejectsInvalidJsonFromProvider() {
        when(client.profile("octocat")).thenReturn("invalid");
        assertEquals(502, assertThrows(ResponseStatusException.class,
                () -> service.preview("octocat", 1)).getStatusCode().value());
    }
}
