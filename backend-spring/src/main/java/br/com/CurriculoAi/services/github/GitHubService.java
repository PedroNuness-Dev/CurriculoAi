package br.com.CurriculoAi.services.github;

import br.com.CurriculoAi.client.github.GitHubClient;
import br.com.CurriculoAi.dto.github.*;
import br.com.CurriculoAi.dto.project.ProjectRequest;
import br.com.CurriculoAi.dto.project.ProjectResponse;
import br.com.CurriculoAi.services.auth.AuthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GitHubService {
    private static final Pattern PROFILE = Pattern.compile(
            "^(?:https://github\\.com/)?([A-Za-z0-9](?:[A-Za-z0-9-]{0,37}[A-Za-z0-9])?)/?$",
            Pattern.CASE_INSENSITIVE);
    private final GitHubClient client;
    private final GitHubImportWriter writer;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public static String username(String input) {
        var match = PROFILE.matcher(input == null ? "" : input.strip());
        if (!match.matches() || match.group(1).contains("--")) {
            throw new IllegalArgumentException("Informe o usuário ou o link do perfil no GitHub.");
        }
        return match.group(1);
    }

    public GitHubPreview preview(String input, int page) {
        String username = username(input);
        if (page < 1 || page > 1000) throw new IllegalArgumentException("Página inválida.");
        JsonNode profile = read(() -> client.profile(username));
        JsonNode repositories = read(() -> client.repositories(username, page));
        if (!repositories.isArray() || !profile.path("login").isTextual()) throw invalidResponse();
        List<GitHubRepository> items = new ArrayList<>();
        for (JsonNode repo : repositories) {
            if (!repo.path("private").asBoolean()) items.add(new GitHubRepository(
                    repo.path("name").asText(), repo.path("description").asText(""),
                    repositoryUrl(repo), repo.path("language").asText(""),
                    repo.path("fork").asBoolean(), repo.path("archived").asBoolean()));
        }
        return new GitHubPreview(profile.path("login").asText(), profile.path("name").asText(""),
                profile.path("bio").asText(""), profile.path("location").asText(""),
                "https://github.com/" + profile.path("login").asText(), items, repositories.size() == 30, page);
    }

    public List<ProjectResponse> importProjects(GitHubImportRequest request) {
        String username = username(request.username());
        List<ProjectRequest> projects = new ArrayList<>();
        for (var selection : request.projects()) {
            JsonNode repo = read(() -> client.repository(username, selection.repository()));
            if (repo.path("private").asBoolean() ||
                    !username.equalsIgnoreCase(repo.path("owner").path("login").asText())) {
                throw new IllegalArgumentException("Selecione repositórios públicos do perfil consultado.");
            }
            projects.add(new ProjectRequest(selection.title().strip(), selection.description().strip(),
                    repositoryUrl(repo), List.of()));
        }
        return writer.save(authService.getUserByTokenAuthenticate().getUserId(), projects);
    }

    private String repositoryUrl(JsonNode repo) {
        String owner = repo.path("owner").path("login").asText();
        String name = repo.path("name").asText();
        if (!PROFILE.matcher(owner).matches() || !name.matches("[A-Za-z0-9_.-]{1,100}")) throw invalidResponse();
        return "https://github.com/" + owner + "/" + name;
    }

    private JsonNode read(Supplier<String> operation) {
        try {
            return mapper.readTree(operation.get());
        } catch (FeignException e) {
            if (e.status() == 404) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil ou repositório não encontrado.");
            if (e.status() == 403 || e.status() == 429) throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Limite de consultas do GitHub atingido. Tente novamente mais tarde.");
            throw invalidResponse();
        } catch (java.io.IOException e) {
            throw invalidResponse();
        }
    }

    private ResponseStatusException invalidResponse() {
        return new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Não foi possível consultar o GitHub.");
    }
}
