package br.com.CurriculoAi.enums.careergoal;

public enum GoalType {
    PRIMEIRO_EMPREGO("PRIMEIRO_EMPREGO"),
    ESTAGIO("ESTAGIO"),
    PROGRESSAO_CARREIRA("PROGRESSAO_CARREIRA"),
    MUDANCA_DE_AREA("MUDANCA_DE_AREA"),
    PREPARACAO_MERCADO("PREPARACAO_MERCADO");

    private String tipo;

    GoalType(String tipo) {
        this.tipo = tipo;
    }

    public static GoalType from(String tipoBuscado){

        GoalType goalType = null;

        for (GoalType g : values()){
            if (g.tipo.equalsIgnoreCase(tipoBuscado)){
                goalType = g;
            }
        }

        if (goalType == null) throw new IllegalArgumentException("Tipo de objetivo de carreira não encontrado");

        return goalType;
    }
}