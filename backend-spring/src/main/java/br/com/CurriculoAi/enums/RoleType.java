package br.com.CurriculoAi.enums;

public enum RoleType {

    ROLE_CANDIDATO("ROLE_CANDIDATO"),
    ROLE_RECRUTADOR("ROLE_RECRUTADOR");

    private final String name;

    RoleType(String name) {
        this.name = name;
    }

    public static RoleType from(String nameTypeVerify){
        for (RoleType roleType : values()){
            if (roleType.name.equalsIgnoreCase(nameTypeVerify)){
                return roleType;
            }
        }

        throw new IllegalArgumentException("Valor inválido para ROLE");
    }
}
