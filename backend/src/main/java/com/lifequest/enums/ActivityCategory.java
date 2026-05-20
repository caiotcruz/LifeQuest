package com.lifequest.enums;

public enum ActivityCategory {
    HEALTH("Saúde e Fitness", 20),
    STUDY("Estudos", 25),
    WORK("Trabalho", 30),
    PERSONAL_DEVELOPMENT("Desenvolvimento Pessoal", 20),
    CREATIVITY("Criatividade", 15);

    private final String label;
    private final int baseXp;

    ActivityCategory(String label, int baseXp) {
        this.label = label;
        this.baseXp = baseXp;
    }

    public String getLabel() { return label; }
    public int getBaseXp()   { return baseXp; }
}