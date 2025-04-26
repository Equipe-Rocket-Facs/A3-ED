package com.equiperocket.projects.cinema;

import java.util.*;

public class Guiche {
    private final int id;
    private final Queue<Cliente> fila;
    private boolean ativo;
    private int totalClientesAtendidos = 0;

    public Guiche(int id) {
        this.id = id;
        this.fila = new LinkedList<>();
        this.ativo = true;
    }

    public void adicionarCliente(Cliente cliente) {
        if (cliente != null) {
            fila.offer(cliente);
        }
    }

    public Cliente atenderCliente() {
        Cliente cliente = fila.poll();
        if (cliente != null) {
            totalClientesAtendidos++;
        }
        return cliente;
    }

    public Queue<Cliente> getFila() {
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
        return fila.size();
    }

    public void ordenarFilaPorPrioridade() {
        List<Cliente> listaTemporaria = new ArrayList<>(fila);
        Collections.sort(listaTemporaria);
        fila.clear();
        fila.addAll(listaTemporaria);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Guichê ")
                .append(id)
                .append(" (")
                .append(ativo ? "Ativo" : "Pausado")
                .append(") -> ")
                .append(fila)
                .toString();
    }
}
