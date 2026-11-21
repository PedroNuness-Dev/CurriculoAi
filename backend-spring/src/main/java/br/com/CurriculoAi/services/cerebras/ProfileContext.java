package br.com.CurriculoAi.services.cerebras;

import br.com.CurriculoAi.dto.project.ProjectResponse;
import java.util.*;

/** Removes exact repeated sentences and groups identical descriptions without inventing summaries. */
public final class ProfileContext {
    private ProfileContext() {}
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
