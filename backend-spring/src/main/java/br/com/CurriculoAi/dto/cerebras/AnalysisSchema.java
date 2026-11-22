package br.com.CurriculoAi.dto.cerebras;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Formato solicitado à Cerebras antes da validação dos valores e das citações. */
public final class AnalysisSchema {
    private AnalysisSchema() {}

    private static Map<String, Object> object(Map<String, ?> fields) {
        return Map.of("type", "object", "properties", fields, "required", fields.keySet(),
                "additionalProperties", false);
    }

    public static Map<String, Object> score() {
        return object(Map.of("value", Map.of("type", List.of("integer", "null"), "minimum", 0, "maximum", 100),
                "rationale", Map.of("type", "string")));
    }

    public static Map<String, Object> responseFormat(boolean compare) {
        var text = Map.of("type", "string");
        var optional = Map.of("type", List.of("string", "null"));
        var strings = Map.of("type", "array", "items", text);
        var item = object(Map.of("priority", Map.of("type", "string", "enum", List.of("HIGH","MEDIUM","LOW")),
                "section", text, "originalExcerpt", text, "problem", text, "suggestion", text,
                "example", optional, "question", optional));
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("summary", text);
        fields.put("structureScore", score());
        fields.put("contentScore", score());
        fields.put("strengths", strings);
        fields.put("weaknesses", strings);
        fields.put("suggestions", strings);
        fields.put("improvements", Map.of("type", "array", "items", item));
        if (compare) fields.put("compatibility", object(Map.of("summary", text, "matchedRequirements", strings,
                "requirementsWithoutEvidence", strings, "score", score())));
        return Map.of("type", "json_schema", "json_schema", Map.of("name", "diagnostico",
                "strict", true, "schema", object(fields)));
    }
}
