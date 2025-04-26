package com.equiperocket.projects.cinema;

public enum TipoClient {
    NORMAL(3),
    ESTUDANTE(2),
    IDOSO(1);

    private final int prioridade;

    TipoClient(int prioridade) {
        this.prioridade = prioridade;
    }

    public int getPrioridade() {
        return prioridade;
    }
}