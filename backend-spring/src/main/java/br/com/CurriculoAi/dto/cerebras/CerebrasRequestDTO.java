package br.com.CurriculoAi.dto.cerebras;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record CerebrasRequestDTO(String model, List<MessageDTO> messages,
        @JsonProperty("response_format") @JsonInclude(JsonInclude.Include.NON_NULL) Map<String, Object> responseFormat) {
    @JsonProperty("max_completion_tokens")
    public int maxCompletionTokens() {
        if (responseFormat != null && responseFormat.get("json_schema") instanceof Map<?, ?> schema
                && "resume".equals(schema.get("name"))) return 8192;
        return 4096;
    }
    public CerebrasRequestDTO(String model, List<MessageDTO> messages) {
        this(model, messages, null);
    }

    public CerebrasRequestDTO {
        if (model == null || model.isBlank()) throw new IllegalArgumentException("Modelo da IA não configurado.");
        messages = List.copyOf(messages);
    }
}
