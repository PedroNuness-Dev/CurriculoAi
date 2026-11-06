package br.com.CurriculoAi.enums.evaluation;

public enum EvaluationStatus {
    PENDING("PENDING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED");

    private String status;

    EvaluationStatus(String status) {
        this.status = status;
    }

    public static EvaluationStatus from(String statusBuscado){

        EvaluationStatus evaluationStatus = null;

        for (EvaluationStatus s : values()){
            if (s.status.equalsIgnoreCase(statusBuscado)){
                evaluationStatus = s;
            }
        }

        if (evaluationStatus == null) throw new IllegalArgumentException("Status de avaliação não encontrado");

        return evaluationStatus;
    }
}