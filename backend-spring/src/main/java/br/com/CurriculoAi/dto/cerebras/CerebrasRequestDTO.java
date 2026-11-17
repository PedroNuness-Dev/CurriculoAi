package br.com.CurriculoAi.dto.cerebras;

import java.util.List;

public class CerebrasRequestDTO {

    private String model;
    private List<MessageDTO> messages;

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
