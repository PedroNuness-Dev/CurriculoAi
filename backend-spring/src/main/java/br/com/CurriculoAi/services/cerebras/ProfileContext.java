package br.com.CurriculoAi.services.cerebras;

import br.com.CurriculoAi.dto.project.ProjectResponse;
import java.util.*;

/** Removes exact repeated sentences and groups identical descriptions without inventing summaries. */
public final class ProfileContext {
    private ProfileContext() {}
    public static String groundProjectSections(String markdown, List<ProjectResponse> projects) {
        if (projects == null) return markdown;
        var sections = markdown.split("(?m)(?=^### )");
        var result = new StringBuilder();
        for (String section : sections) {
            int nextHeading = section.indexOf("\n## ");
            String block = nextHeading < 0 ? section : section.substring(0, nextHeading);
            var project = projects.stream().filter(p -> p.link() != null && !p.link().isBlank()
                    && block.startsWith("### ") && block.contains(p.link())).findFirst();
            if (project.isPresent()) {
                var p = project.get();
                result.append("### ").append(p.title()).append("\n\n").append(p.link()).append("\n\n");
                Arrays.stream(uniqueSentences(p.description()).split("(?<=[.!?])\\s+"))
                        .filter(sentence -> !sentence.isBlank() && !sentence.startsWith("Não ")).limit(3)
                        .forEach(sentence -> result.append("- ").append(sentence).append("\n"));
                result.append("\n");
                if (nextHeading >= 0) result.append(section.substring(nextHeading));
            } else result.append(section);
        }
        return result.toString();
    }
    public static String uniqueSentences(String text) {
        if (text == null) return "";
        return String.join(" ", new LinkedHashSet<>(Arrays.asList(text.strip().split("(?<=[.!?])\\s+"))));
    }
    public static List<Map<String, Object>> projects(List<ProjectResponse> projects) {
        if (projects == null) return List.of();
        var groups = new LinkedHashMap<String, Map<String, Object>>();
        for (var project : projects) {
            String description = uniqueSentences(project.description());
            var group = groups.computeIfAbsent(description, key -> {
                var value = new LinkedHashMap<String, Object>();
                value.put("description", key);
                value.put("projects", new ArrayList<Map<String, Object>>());
                return value;
            });
            @SuppressWarnings("unchecked")
            var items = (List<Map<String, Object>>) group.get("projects");
            var item = new LinkedHashMap<String, Object>();
            item.put("title", project.title());
            item.put("link", project.link());
            item.put("abilities", project.abilities());
            items.add(item);
        }
        return List.copyOf(groups.values());
    }
}
