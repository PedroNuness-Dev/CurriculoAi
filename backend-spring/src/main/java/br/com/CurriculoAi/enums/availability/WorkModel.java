package br.com.CurriculoAi.enums.availability;

public enum WorkModel {
    PRESENCIAL("PRESENCIAL"),
    REMOTO("REMOTO"),
    HIBRIDO("HIBRIDO");

    private String tipo;

    WorkModel(String tipo) {
        this.tipo = tipo;
    }

    public static WorkModel from(String tipoBuscado){

        WorkModel modeloDetrabalho = null;

        for (WorkModel m : values()){
            if (m.tipo.equalsIgnoreCase(tipoBuscado)){
                modeloDetrabalho = m;
            }
        }

        if (modeloDetrabalho == null) throw new IllegalArgumentException("Tipo de modelo de trabalho não encontrado");

        return modeloDetrabalho;
    }
}
