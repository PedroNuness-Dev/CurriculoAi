package br.com.CurriculoAi.dto.cerebras;
/** Avaliação editorial explicada, não uma probabilidade de aprovação em ATS. */
public record AssessmentScore(Integer value, String rationale) {
    public AssessmentScore {
        if (value != null && (value < 0 || value > 100) || rationale == null || rationale.isBlank())
            throw new IllegalArgumentException("Nota ou justificativa inválida");
    }
}
