package br.com.CurriculoAi.enums.language;

public enum LanguageEnum {

    INGLES("Inglês"),
    MANDARIM("Mandarim"),
    HINDI("Hindi"),
    ESPANHOL("Espanhol"),
    FRANCES("Francês"),
    ARABE("Árabe"),
    BENGALI("Bengali"),
    PORTUGUES("Português"),
    RUSSO("Russo"),
    URDU("Urdu"),
    INDONESIO("Indonésio"),
    ALEMAO("Alemão"),
    JAPONES("Japonês"),
    SUAILI("Suaíli"),
    MARATA("Marata"),
    TELUGU("Telugu"),
    TURCO("Turco"),
    TAMIL("Tâmil"),
    VIETNAMITA("Vietnamita"),
    COREANO("Coreano"),
    PUNJABI("Punjabi"),
    JAVANES("Javanês"),
    ITALIANO("Italiano"),
    PERSA("Persa"),
    GUJARATI("Gujarati"),
    BOJPURI("Bhojpuri"),
    MIN_NAN("Min Nan"),
    HAUCA("Hauçá"),
    CANARES("Canarês"),
    MALAYALAM("Malaiala"),
    ODIA("Odia"),
    BIRMANES("Birmanês"),
    POLONES("Polonês"),
    UCRANIANO("Ucraniano"),
    HOLANDES("Holandês"),
    TAILANDES("Tailandês"),
    TAGALO("Tagalo"),
    IORUBA("Iorubá"),
    AMARICO("Amárico"),
    ROMENO("Romeno"),
    UZBEQUE("Uzbeque"),
    SINDHI("Sindi"),
    IGBO("Igbo"),
    NEPALI("Nepalês"),
    CEBUANO("Cebuano"),
    SERVO_CROATA("Servo-croata"),
    MALGAXE("Malgaxe"),
    SOMALI("Somali"),
    CINGALES("Cingalês");

    private final String descricao;

    LanguageEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static LanguageEnum from(String idioma) {
        if (idioma == null) return null;

        for (LanguageEnum language : values()) {
            if (language.name().equals(idioma.toUpperCase())) {
                return language;
            }
        }

        throw new IllegalArgumentException("Idioma inválido: " + idioma);
    }
}