package com.equiperocket.projects;

import com.equiperocket.projects.cinema.Cinema;
import com.equiperocket.projects.cinema.Cliente;
import com.equiperocket.projects.cinema.TipoClient;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MainCinema {
    private static final String MENU = """
            
            === Menu do Cinema ===
            1. Adicionar Cliente
            2. Pausar Guichê
            3. Atender Cliente
            4. Exibir Filas
            5. Ativar Guichê
            0. Sair
            """;

    private static final Scanner scanner = new Scanner(System.in);
    private static Cinema cinema;

    public static void main(String[] args) {
        inicializarCinema();
        executarMenuPrincipal();
        encerrarPrograma();
    }

    private static void inicializarCinema() {
        while (true) {
            try {
                System.out.println("Digite o número de guichês:");
                int numeroGuiches = lerInteiro();
                if (numeroGuiches <= 0) {
                    System.out.println("O número de guichês deve ser positivo.");
                    continue;
                }
                cinema = new Cinema(numeroGuiches);
                cinema.adicionarClientesIniciais(cinema);
                break;
            } catch (InputMismatchException e) {
                System.out.println("Por favor, digite um número válido.");
                scanner.nextLine();
            }
        }
    }

    private static void executarMenuPrincipal() {
        int opcao;
        do {
            System.out.println(MENU);
            opcao = processarOpcaoMenu();
        } while (opcao != 0);
    }

    private static int processarOpcaoMenu() {
        try {
            int opcao = lerInteiro();
            executarAcaoMenu(opcao);
            return opcao;
        } catch (InputMismatchException e) {
            System.out.println("Erro: Digite um número válido!");
            scanner.nextLine();
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
            case 3 -> atenderCliente();
            case 4 -> cinema.exibirFilas();
            case 5 -> ativarGuiche();
            case 0 -> System.out.println("Encerrando o programa...");
            default -> System.out.println("Opção inválida!");
        }
    }

    private static void adicionarCliente() {
        System.out.println("Tipo do cliente (1-Normal, 2-Estudante, 3-Idoso):");
        try {
            int tipo = lerInteiro();
            TipoClient tipoCliente = switch (tipo) {
                case 1 -> TipoClient.NORMAL;
                case 2 -> TipoClient.ESTUDANTE;
                case 3 -> TipoClient.IDOSO;
                default -> {
                    System.out.println("Tipo inválido! Cliente será adicionado como Normal.");
                    yield TipoClient.NORMAL;
                }
            };
            cinema.adicionarCliente(new Cliente(tipoCliente));
            System.out.println("Cliente adicionado com sucesso!");
        } catch (InputMismatchException e) {
            System.out.println("Tipo inválido! Cliente não foi adicionado.");
            scanner.nextLine();
        }
    }

    private static void pausarGuiche() {
        try {
            System.out.println("ID do guichê para pausar:");
            int id = lerInteiro();
            cinema.pausarGuiche(id);
            System.out.println("Guichê " + id + " pausado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao pausar guichê: " + e.getMessage());
        }
    }

    private static void atenderCliente() {
        try {
            System.out.println("ID do guichê para atender:");
            int id = lerInteiro();
            cinema.atenderCliente(id);
            System.out.println("Cliente atendido no guichê " + id);
        } catch (Exception e) {
            System.out.println("Erro ao atender cliente: " + e.getMessage());
        }
    }

    private static void ativarGuiche() {
        try {
            System.out.println("ID do guichê para ativar:");
            int id = lerInteiro();
            cinema.reativarGuiche(id);
            System.out.println("Guichê " + id + " ativado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao ativar guichê: " + e.getMessage());
        }
    }

    private static int lerInteiro() {
        return scanner.nextInt();
    }

    private static void encerrarPrograma() {
        scanner.close();
    }
}
