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

    public static Map<String, Object> responseFormat(boolean compare) {
        var text = Map.of("type", "string");
        var optional = Map.of("type", List.of("string", "null"));
        var strings = Map.of("type", "array", "items", text);
        var item = object(Map.of("prioridade", Map.of("type", "string", "enum", List.of("ALTA","MEDIA","BAIXA")),
                "secao", text, "trechoOriginal", text, "problema", text, "sugestao", text,
                "exemplo", optional, "perguntaAoUsuario", optional));
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("resumo", text);
        fields.put("pontosFortes", strings);
        fields.put("pontosDeMelhoria", strings);
        fields.put("sugestoes", strings);
        fields.put("melhorias", Map.of("type", "array", "items", item));
        if (compare) fields.put("compatibilidade", object(Map.of("resumo", text, "requisitosAtendidos", strings,
                "requisitosSemEvidencia", strings)));
        return Map.of("type", "json_schema", "json_schema", Map.of("name", "diagnostico",
                "strict", true, "schema", object(fields)));
    }
}
