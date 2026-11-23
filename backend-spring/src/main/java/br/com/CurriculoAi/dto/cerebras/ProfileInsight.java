package br.com.CurriculoAi.dto.cerebras;
public record ProfileInsight(String skill, String evidence, String interpretation, String question) {
    public ProfileInsight {
        if (skill == null || skill.isBlank() || evidence == null || evidence.isBlank()
                || interpretation == null || interpretation.isBlank() || question == null || question.isBlank())
            throw new IllegalArgumentException("Descoberta sem evidência ou pergunta");
    }
}
