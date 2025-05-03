package com.equiperocket.projects.cinema;

import java.util.*;

public class Guiche {
    private final int id;
    private final Fila<Cliente> fila;
    private boolean ativo;
    private int totalClientesAtendidos = 0;

    public Guiche(int id) {
        this.id = id;
        this.fila = new Fila<>();
        this.ativo = true;
    }

    public void adicionarCliente(Cliente cliente) {
        if (cliente != null) {
            fila.enfileirar(cliente);
        }
    }

    public Cliente atenderCliente() {
        Cliente cliente = fila.desenfileirar();
        if (cliente != null) {
            totalClientesAtendidos++;
        }
        return cliente;
    }

    public Fila<Cliente> getFila() {
        return fila;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    public int getId() {
        return id;
    }

    public int getTotalClientesAtendidos() {
        return totalClientesAtendidos;
    }

    public int tamanhoFila() {
        return fila.tamanho();
    }

    public void ordenarFilaPorPrioridade() {
        if (fila.estaVazia()) {
            return;
        }

        List<Cliente> clientesOrdenados = new ArrayList<>();
        while (!fila.estaVazia()) {
            clientesOrdenados.add(fila.desenfileirar());
        }

        Collections.sort(clientesOrdenados);

        for (Cliente cliente : clientesOrdenados) {
            fila.enfileirar(cliente);
        }
    }

    @Override
    public String toString() {
        StringBuilder clientesStr = new StringBuilder();
        for (Cliente cliente : fila) {
            clientesStr.append(cliente.toString()).append(", ");
        }
        if (!clientesStr.isEmpty()) {
            clientesStr.setLength(clientesStr.length() - 2);
        }
        return new StringBuilder()
                .append("Guichê ")
                .append(id)
                .append(" (")
                .append(ativo ? "Ativo" : "Pausado")
                .append(") -> ")
                .append(clientesStr)
                .toString();
    }
}
