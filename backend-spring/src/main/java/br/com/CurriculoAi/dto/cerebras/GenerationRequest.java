package br.com.CurriculoAi.dto.cerebras;
public record GenerationRequest(String jobDescription, String answers) {
    public GenerationRequest {
        if ((jobDescription != null && jobDescription.length() > 16000)
                || (answers != null && answers.length() > 16000))
            throw new IllegalArgumentException("Cada campo aceita até 16 mil caracteres.");
    }
}
