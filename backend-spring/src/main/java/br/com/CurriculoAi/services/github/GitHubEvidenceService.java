package br.com.CurriculoAi.services.github;

import br.com.CurriculoAi.client.github.GitHubClient;
import br.com.CurriculoAi.dto.project.ProjectResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/** Amostra limitada de arquivos públicos. Não comprova autoria nem domínio das tecnologias. */
@Service
@RequiredArgsConstructor
public class GitHubEvidenceService {
    private final GitHubClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Pattern REPOSITORY = Pattern.compile("^https://github\\.com/([A-Za-z0-9-]{1,39})/([A-Za-z0-9_.-]{1,100})/?$");

    public List<Map<String, Object>> inspect(List<ProjectResponse> projects) {
        if (projects == null) return List.of();
        var results = new ArrayList<Map<String, Object>>();
        var seen = new HashSet<String>();
        for (var project : projects) {
            var match = REPOSITORY.matcher(project.link() == null ? "" : project.link());
            if (!match.matches() || !seen.add(project.link())) continue;
            if (results.size() == 3) break;
            var result = new LinkedHashMap<String, Object>();
            result.put("repository", project.link());
            result.put("scope", "Amostra de até 3 arquivos; autoria e contribuição individual não verificadas.");
            results.add(result);
            var files = new ArrayList<Map<String, String>>();
            result.put("files", files);
            try {
                var repo = mapper.readTree(client.repository(match.group(1), match.group(2)));
                if (repo.path("private").asBoolean()) { result.put("status", "Não público"); continue; }
                var tree = mapper.readTree(client.tree(match.group(1), match.group(2), repo.path("default_branch").asText("HEAD")));
                var candidates = new ArrayList<com.fasterxml.jackson.databind.JsonNode>();
                for (var item : tree.path("tree")) {
                    String path = item.path("path").asText();
                    if (!item.path("type").asText().equals("blob") || item.path("size").asInt(65537) > 65536) continue;
                    if (path.matches("(?i).*(node_modules|vendor|dist|build|target|lock|generated).*")) continue;
                    if (path.matches("(?i)(readme\\.md|pom\\.xml|package\\.json|.*\\.(java|py|ts|tsx|go|rs))")) candidates.add(item);
                }
                candidates.sort(Comparator.comparingInt(n -> n.path("path").asText().equalsIgnoreCase("README.md") ? 0
                        : n.path("path").asText().matches("pom.xml|package.json") ? 1 : 2));
                for (var item : candidates.stream().limit(3).toList()) {
                    String sha = item.path("sha").asText();
                    if (!sha.matches("[a-fA-F0-9]{40,64}")) continue;
                    var blob = mapper.readTree(client.blob(match.group(1), match.group(2), sha));
                    if (!blob.path("encoding").asText().equals("base64") || blob.path("size").asInt(65537) > 65536) continue;
                    String content = new String(Base64.getMimeDecoder().decode(blob.path("content").asText()), StandardCharsets.UTF_8);
                    files.add(Map.of("path", item.path("path").asText(), "sha", sha,
                            "excerpt", content.substring(0, Math.min(content.length(), 4000))));
                }
                result.put("status", files.isEmpty() ? "Sem arquivos elegíveis" : "Amostra parcial consultada");
            } catch (Exception e) {
                result.put("status", "Consulta indisponível; não inferir ausência de competências");
            }
        }
        return List.copyOf(results);
    }
}
