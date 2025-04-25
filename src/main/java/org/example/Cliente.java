package org.example;

import java.util.UUID;

public class Cliente implements Comparable<Cliente>{
    private final String id;
    private final TipoClient tipo;

    public Cliente(TipoClient tipo) {
        this.id = UUID.randomUUID().toString();
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public TipoClient getTipo() {
        return tipo;
    }

    @Override
    public int compareTo(Cliente outroCliente) {
        if (outroCliente == null) {
            return -1;
        }

        return Integer.compare(
                this.tipo.getPrioridade(),
                outroCliente.tipo.getPrioridade()
        );
    }

    @Override
    public String toString() {
        return tipo + "-" + id.substring(0, 5);
    }
}

