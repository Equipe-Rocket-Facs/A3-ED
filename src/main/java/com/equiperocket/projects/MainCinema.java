package com.equiperocket.projects;

import com.equiperocket.projects.cinema.*;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class MainCinema {
    private static final String MENU = """
            
            === Menu do Cinema ===
            1. Adicionar Cliente
            2. Pausar Guichê
            3. Atendimento
            4. Exibir Filas
            5. Ativar Guichê
            0. Sair
            """;

    private static final Scanner scanner = new Scanner(System.in);
    private static final AtomicReference<Cinema> cinema = new AtomicReference<>();

    public static void main(String[] args) {
        try {
            inicializarCinema();
            executarMenuPrincipal();
        } finally {
            encerrarPrograma();
        }
    }

    private static void inicializarCinema() {
        lerEntradaValida("Digite o número de guichês:", () -> {
            int numeroGuiches = lerInteiro();
            if (numeroGuiches <= 0) {
                throw new IllegalArgumentException("O número de guichês deve ser positivo.");
            }
            Cinema novoCinema = new Cinema(numeroGuiches);
            novoCinema.adicionarClientesIniciais(novoCinema);
            cinema.set(novoCinema);
            return true;
        });
    }

    private static void executarMenuPrincipal() {
        while (true) {
            System.out.println(MENU);
            int opcao = processarOpcaoMenu();
            if (opcao == 0) break;
        }
    }

    private static int processarOpcaoMenu() {
        try {
            int opcao = lerInteiro();
            executarAcaoMenu(opcao);
            return opcao;
        } catch (InputMismatchException e) {
            tratarErroEntrada("Erro: Digite um número válido!");
            return -1;
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return -1;
        }
    }

    private static void executarAcaoMenu(int opcao) {
        switch (opcao) {
            case 1 -> adicionarCliente();
            case 2 -> pausarGuiche();
            case 3 -> atendimento();
            case 4 -> cinema.get().exibirFilas();
            case 5 -> ativarGuiche();
            case 0 -> System.out.println("Encerrando o programa...");
            default -> System.out.println("Opção inválida!");
        }
    }

    private static void adicionarCliente() {
        System.out.println("Tipo do cliente (1-Normal, 2-Estudante, 3-Idoso):");
        try {
            TipoClient tipoCliente = obterTipoCliente(lerInteiro());
            System.out.println("Digite a quantidade de clientes do tipo " + tipoCliente + " que deseja adicionar:");
            int numeroClientes = lerInteiro();
            adicionarMultiplosClientes(tipoCliente, numeroClientes);
            System.out.println("Clientes adicionados com sucesso!");
        } catch (InputMismatchException e) {
            tratarErroEntrada("Tipo inválido! Cliente não foi adicionado.");
        }
    }

    private static TipoClient obterTipoCliente(int tipo) {
        return switch (tipo) {
            case 1 -> TipoClient.NORMAL;
            case 2 -> TipoClient.ESTUDANTE;
            case 3 -> TipoClient.IDOSO;
            default -> {
                System.out.println("Tipo inválido! Cliente será adicionado como Normal.");
                yield TipoClient.NORMAL;
            }
        };
    }

    private static void adicionarMultiplosClientes(TipoClient tipo, int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            cinema.get().adicionarCliente(new Cliente(tipo));
        }
    }

    private static void pausarGuiche() {
        lerEntradaValida("ID do guichê para pausar:", () -> {
            int id = lerInteiro();
            cinema.get().pausarGuiche(id);
            System.out.println("Guichê " + id + " pausado com sucesso!");
            return true;
        });
    }

    private static void atendimento() {
        while (true) {
            System.out.println("1- Atender cliente");
            System.out.println("2- Iniciar atendimento automático");
            System.out.println("3- Parar atendimento automático");
            System.out.println("4- Voltar ao menu principal");
            try {
                int opcaoAtendimento = lerInteiro();
                switch (opcaoAtendimento) {
                    case 1 -> atenderClienteManual();
                    case 2 -> {
                        System.out.println("Digite o tempo de atendimento(em segundos): ");
                        int tempoAtendimento = lerInteiro();
                        cinema.get().iniciarAtendimentoAutomatico(tempoAtendimento);
                    }
                    case 3 -> cinema.get().pararAtendimentoAutomatico();
                    case 4 -> {
                        return;
                    }
                    default -> System.out.println("Opção inválida!");
                }
            } catch (InputMismatchException e) {
                tratarErroEntrada("Opção inválida! Por favor, digite um número.");
            }
        }
    }

    private static void atenderClienteManual() {
        lerEntradaValida("ID do guichê para atender:", () -> {
            int id = lerInteiro();
            cinema.get().atenderCliente(id);
            return true;
        });
    }

    private static void ativarGuiche() {
        lerEntradaValida("ID do guichê para ativar:", () -> {
            int id = lerInteiro();
            cinema.get().reativarGuiche(id);
            System.out.println("Guichê " + id + " ativado com sucesso!");
            return true;
        });
    }

    private static void lerEntradaValida(String mensagem, Supplier<Boolean> acao) {
        while (true) {
            try {
                System.out.println(mensagem);
                if (acao.get()) break;
            } catch (InputMismatchException e) {
                tratarErroEntrada("Por favor, digite um número válido.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void tratarErroEntrada(String mensagem) {
        System.out.println(mensagem);
        scanner.nextLine();
    }

    private static int lerInteiro() {
        return scanner.nextInt();
    }

    private static void encerrarPrograma() {
        scanner.close();
    }
}
