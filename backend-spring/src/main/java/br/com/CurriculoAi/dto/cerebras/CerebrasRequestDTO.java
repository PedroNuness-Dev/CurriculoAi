package br.com.CurriculoAi.dto.cerebras;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CerebrasRequestDTO {

    private String model;
    private List<MessageDTO> messages;
    @JsonProperty("response_format")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> responseFormat;

    public CerebrasRequestDTO(List<MessageDTO> messages, Map<String, Object> responseFormat) {
        this(messages);
        this.responseFormat = responseFormat;
    }

    public CerebrasRequestDTO(String prompt) {
        this(List.of(new MessageDTO("user", prompt)));
    }

    public CerebrasRequestDTO(List<MessageDTO> messages) {

        //this.model = "llama3.1-8b";
        this.model = "gpt-oss-120b";

        this.messages = List.copyOf(messages);
    }

    public String getModel() {
        return model;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }
}
