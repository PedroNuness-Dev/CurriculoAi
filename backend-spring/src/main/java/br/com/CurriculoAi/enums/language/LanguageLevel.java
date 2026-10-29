package br.com.CurriculoAi.enums.language;

public enum LanguageLevel {

    BASICO("Básico"),
    INTERMEDIARIO("Intermediário"),
    AVANCADO("Avançado"),
    FLUENTE("Fluente"),
    NATIVO("Nativo");

    private final String descricao;

    LanguageLevel(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static LanguageLevel from(String nivel) {
        if (nivel == null) return null;

        for (LanguageLevel languageLevel : values()) {
            if (languageLevel.name().equals(nivel.toUpperCase())) {
                return languageLevel;
            }
        }

        throw new IllegalArgumentException("Nível de idioma inválido: " + nivel);
    }
}
