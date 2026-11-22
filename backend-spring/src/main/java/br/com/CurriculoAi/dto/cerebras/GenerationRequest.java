package br.com.CurriculoAi.dto.cerebras;
public record GenerationRequest(String jobDescription, String answers, String outputLanguage) {
    public GenerationRequest(String jobDescription, String answers) {
        this(jobDescription, answers, null);
    }
    public GenerationRequest {
        if ((jobDescription != null && jobDescription.length() > 16000)
                || (answers != null && answers.length() > 16000))
            throw new IllegalArgumentException("Cada campo aceita até 16 mil caracteres.");
        if (outputLanguage == null || outputLanguage.isBlank()) outputLanguage = "pt-BR";
        if (!outputLanguage.equals("pt-BR") && !outputLanguage.equals("en-US"))
            throw new IllegalArgumentException("Idioma de saída inválido. Use pt-BR ou en-US.");
    }
}
