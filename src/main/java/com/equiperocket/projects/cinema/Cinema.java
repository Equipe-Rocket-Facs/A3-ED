package com.equiperocket.projects.cinema;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Cinema {
    private final List<Guiche> guiches;
    private boolean[] guichesPausados;
    private ScheduledExecutorService executor;

    public Cinema(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("O número de guichês deve ser maior que zero");
        }
        guiches = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            guiches.add(new Guiche(i));
        }
        guichesPausados = new boolean[n];
    }

    public void adicionarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }

        if (guiches.stream().noneMatch(Guiche::isAtivo)) {
            throw new IllegalStateException("Não há guichês ativos para atender clientes");
        }

        Guiche guicheMenorFila = guiches.stream()
                .filter(Guiche::isAtivo)
                .min(Comparator.comparingInt(Guiche::tamanhoFila))
                .orElseThrow(() -> new IllegalStateException("Não foi possível encontrar um guichê disponível"));

        guicheMenorFila.adicionarCliente(cliente);
    }

    public void pausarGuiche(int id) {
        Guiche guiche = guiches.get(id);

        if(guiche == null){
            throw new RuntimeException("Guiche não encontrado");
        }
        if(!guiche.isAtivo()){
            throw new RuntimeException("Guiche está em pausa");
        }

        // Verifica se há apenas 1 guichê ativo
        long guichesAtivos = guiches.stream()
                .filter(Guiche::isAtivo)
                .count();

        if (guichesAtivos <= 1) {
            throw new RuntimeException(
                    "Não é possível redistribuir clientes: apenas um guichê está ativo. " +
                            "É necessário ter mais guichês ativos para realizar a redistribuição."
            );
        }

        guichesPausados[id] = true;
        redistribuirClientes(guiche.getFila());
        guiche.desativar();
    }

    public int getNumeroGuiches() {
        return guichesPausados.length;
    }

    public void reativarGuiche(int id) {
        Guiche guiche = guiches.get(id);

        if (guiche == null) {
            throw new IllegalArgumentException("ID do guichê inválido: " + id);
        }
        if (guiche.isAtivo()) {
            throw new IllegalStateException("Guichê já está ativo");
        }

        guichesPausados[id] = false;
        guiche.ativar();
    }

    public boolean isGuichePausado(int guicheId) {
        return guichesPausados[guicheId];
    }


    private void redistribuirClientes(Fila<Cliente> fila) {
        new Thread(() -> {
            while (!fila.estaVazia()) {
                Cliente cliente = fila.desenfileirar();
                adicionarCliente(cliente);
            }
        }).start();
    }

    public void atenderCliente(int id) {
        Guiche guiche = guiches.get(id);
        guiche.ordenarFilaPorPrioridade();
        Cliente atendido = guiche.atenderCliente();
        System.out.println("Cliente atendido: " + atendido);
    }

    public void iniciarAtendimentoAutomatico(long tempoMedioAtendimentoSegundos) {
        // Se já existe um executor rodando, para ele primeiro
        pararAtendimentoAutomatico();
        long initialDelay = tempoMedioAtendimentoSegundos;
        executor = Executors.newScheduledThreadPool(guiches.size());

        for (Guiche guiche : guiches) {
            executor.scheduleAtFixedRate(() -> {
                if (guiche.isAtivo() && !guiche.getFila().estaVazia()) {
                    guiche.ordenarFilaPorPrioridade();
                    Cliente atendido = guiche.atenderCliente();
                    if (atendido != null) {
                        System.out.println("[Auto] Guichê " + guiche.getId() + " atendeu: " + atendido);
                    }
                }
            }, initialDelay, tempoMedioAtendimentoSegundos, TimeUnit.SECONDS);
        }
    }

    public void pararAtendimentoAutomatico() {
        if (executor != null && !executor.isShutdown()) {
            try {
                // Tenta parar graciosamente primeiro
                executor.shutdown();

                // Espera até 3 segundos para as tarefas terminarem
                if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                    // Se não terminaram, força a parada
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                // Se for interrompido, força a parada
                executor.shutdownNow();
                Thread.currentThread().interrupt(); // Preserva o status de interrupção
            }
        }
    }


    public void exibirFilas() {
        guiches.forEach(guiche -> {
            guiche.ordenarFilaPorPrioridade();
            System.out.println(guiche);
        });
    }

    public List<Fila<Cliente>> getFilas() {
        List<Fila<Cliente>> todasFilas = new ArrayList<>();
        for (Guiche guiche : guiches) {
            todasFilas.add(guiche.getFila());
        }
        return todasFilas;
    }

    // Método para obter uma fila específica de um guichê
    public Fila<Cliente> getFila(int index) {
        if (index >= 0 && index < guiches.size()) {
            return guiches.get(index).getFila();
        }
        return null;
    }

    public Guiche getGuiche(int index) {
        if (index >= 0 && index < guiches.size()) {
            return guiches.get(index);
        }
        return null;
    }

    public int getTotalClientesAtendidos() {
        return guiches.stream()
                .mapToInt(Guiche::getTotalClientesAtendidos)
                .sum();
    }

    public int getTotalClientesNasFilas() {
        return guiches.stream()
                .mapToInt(guiche -> guiche.getFila().tamanho())
                .sum();
    }

    public void adicionarClientesIniciais(Cinema cinema) {
        Cliente[] clientes = {
                new Cliente(TipoClient.NORMAL),
                new Cliente(TipoClient.IDOSO),
                new Cliente(TipoClient.ESTUDANTE),
                new Cliente(TipoClient.NORMAL),
                new Cliente(TipoClient.IDOSO),
                new Cliente(TipoClient.NORMAL),
                new Cliente(TipoClient.IDOSO),
                new Cliente(TipoClient.ESTUDANTE),
                new Cliente(TipoClient.NORMAL),
                new Cliente(TipoClient.IDOSO),
                new Cliente(TipoClient.NORMAL),
                new Cliente(TipoClient.IDOSO),
                new Cliente(TipoClient.ESTUDANTE),
                new Cliente(TipoClient.NORMAL),
                new Cliente(TipoClient.IDOSO),
                new Cliente(TipoClient.NORMAL),
                new Cliente(TipoClient.IDOSO),
                new Cliente(TipoClient.ESTUDANTE),
                new Cliente(TipoClient.NORMAL),
                new Cliente(TipoClient.IDOSO)

        };

        for (Cliente cliente : clientes) {
            cinema.adicionarCliente(cliente);
        }
    }
}
