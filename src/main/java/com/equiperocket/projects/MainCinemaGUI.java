package com.equiperocket.projects;

import com.equiperocket.projects.cinema.Cinema;
import com.equiperocket.projects.cinemaGUI.*;
import com.formdev.flatlaf.FlatIntelliJLaf;
import javax.swing.*;
import java.awt.*;

public class MainCinemaGUI extends JFrame {
    private final Cinema cinema;
    private FilasPanel filasPanel;
    private JLabel statusLabel;
    private JButton atendimentoBtn;
    private boolean atendimentoAutomaticoAtivo = false;

    public MainCinemaGUI(Cinema cinema) {
        this.cinema = cinema;
        configurarJanela();
        iniciarComponentes();
        iniciarTimerAtualizacao();
    }

    private void configurarJanela() {
        setTitle("Sistema de Cinema");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.COR_FUNDO);
    }

    private void iniciarComponentes() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(UIUtils.COR_FUNDO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(UIUtils.COR_FUNDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JButton addClienteBtn = UIUtils.criarBotaoEstilizado("Adicionar Cliente");
        atendimentoBtn = UIUtils.criarBotaoEstilizado("Iniciar Atendimento");
        JButton estatisticasBtn = UIUtils.criarBotaoEstilizado("Estatísticas");

        addClienteBtn.addActionListener(e -> mostrarDialogoAdicionarCliente());
        atendimentoBtn.addActionListener(e -> toggleAtendimentoAutomatico());
        estatisticasBtn.addActionListener(e -> mostrarEstatisticas());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        topPanel.add(addClienteBtn, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.1;
        topPanel.add(atendimentoBtn, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.1;
        topPanel.add(estatisticasBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        gbc.fill = GridBagConstraints.BOTH;

        GuichesPanel guichesPanel = new GuichesPanel(cinema, getStatusLabel(), this::atualizarFilasVisuais);
        JScrollPane guichesScroll = new JScrollPane(guichesPanel);
        guichesScroll.setBorder(null);
        guichesScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        guichesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        guichesScroll.getVerticalScrollBar().setUnitIncrement(16);
        topPanel.add(guichesScroll, gbc);

        filasPanel = new FilasPanel(cinema);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(filasPanel, BorderLayout.CENTER);

        mainPanel.add(getStatusLabel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JLabel getStatusLabel() {
        if (statusLabel == null) {
            statusLabel = new JLabel("Sistema em funcionamento", JLabel.CENTER);
            statusLabel.setBackground(UIUtils.COR_SECUNDARIA);
            statusLabel.setForeground(Color.WHITE);
            statusLabel.setFont(UIUtils.FONTE_PADRAO);
            statusLabel.setOpaque(true);
        }
        return statusLabel;
    }

    private void toggleAtendimentoAutomatico() {
        atendimentoAutomaticoAtivo = !atendimentoAutomaticoAtivo;
        if (atendimentoAutomaticoAtivo) {
            atendimentoBtn.setText("Parar Atendimento");
            atendimentoBtn.setBackground(UIUtils.COR_BOTAO_PARAR);
            int tempo = requisitarTempoAtendimento();
            if (tempo > 0) {
                cinema.iniciarAtendimentoAutomatico(tempo);
                statusLabel.setText("Atendimento automático iniciado");
            } else {
                atendimentoAutomaticoAtivo = false;
                atendimentoBtn.setText("Iniciar Atendimento");
                atendimentoBtn.setBackground(UIUtils.COR_SECUNDARIA);
            }
        } else {
            atendimentoBtn.setText("Iniciar Atendimento");
            atendimentoBtn.setBackground(UIUtils.COR_SECUNDARIA);
            cinema.pararAtendimentoAutomatico();
            statusLabel.setText("Atendimento automático parado");
        }
    }

    private int requisitarTempoAtendimento() {
        try {
            String input = JOptionPane.showInputDialog(this,
                    "Digite o intervalo de tempo em segundos para o atendimento automático (1-60):",
                    "Configurar Atendimento",
                    JOptionPane.QUESTION_MESSAGE);
            int tempo = Integer.parseInt(input);
            if (tempo < 1 || tempo > 60) throw new IllegalArgumentException("O tempo deve estar entre 1 e 60 segundos");
            return tempo;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Por favor, digite um número válido", "Erro", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    private void atualizarFilasVisuais() {
        filasPanel.atualizarSeNecessario();
        if (statusLabel.getForeground().equals(Color.RED)) {
            statusLabel.setForeground(Color.WHITE);
        }
    }

    private void iniciarTimerAtualizacao() {
        Timer refreshTimer = new Timer(1000, e -> atualizarFilasVisuais());
        refreshTimer.start();
    }

    private void mostrarDialogoAdicionarCliente() {
        try {
            AdicionarClienteDialog dialog = new AdicionarClienteDialog(
                    this, cinema, statusLabel, this::atualizarFilasVisuais
            );
            dialog.setVisible(true);
        } catch (Exception e) {
            mostrarErro("Erro ao exibir diálogo", e.getMessage());
        }
    }

    private void mostrarEstatisticas() {
        try {
            EstatisticasDialog dialog = new EstatisticasDialog(this, cinema);
            dialog.setVisible(true);
        } catch (Exception e) {
            mostrarErro("Erro ao exibir estatísticas", e.getMessage());
        }
    }

    private void mostrarErro(String titulo, String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, titulo, JOptionPane.ERROR_MESSAGE);
        if (statusLabel != null) {
            statusLabel.setText("ERRO: " + mensagem);
            statusLabel.setForeground(Color.RED);
        }
        System.err.println(titulo + ": " + mensagem);
    }

    public static void main(String[] args) {
        configurarLookAndFeel();
        iniciarAplicacao();
    }

    private static void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void iniciarAplicacao() {
        boolean entradaValida = false;
        while (!entradaValida) {
            String input = solicitarNumeroGuiches();
            if (input == null) {
                System.exit(0);
                return;
            }
            entradaValida = processarInputGuiches(input);
        }
    }

    private static String solicitarNumeroGuiches() {
        return JOptionPane.showInputDialog(null,
                "Digite o número de guichês desejado (de 1 a 20):",
                "Configuração Inicial",
                JOptionPane.QUESTION_MESSAGE
        );
    }

    private static boolean processarInputGuiches(String input) {
        try {
            int numeroGuiches = Integer.parseInt(input);
            if (numeroGuiches >= 1 && numeroGuiches <= 20) {
                criarEExibirGUI(numeroGuiches);
                return true;
            } else {
                mostrarErroNumeroGuiches();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarErroFormatoInvalido();
            return false;
        }
    }

    private static void criarEExibirGUI(int numeroGuiches) {
        SwingUtilities.invokeLater(() -> {
            Cinema cinema = new Cinema(numeroGuiches);
            cinema.adicionarClientesIniciais(cinema);
            MainCinemaGUI gui = new MainCinemaGUI(cinema);
            gui.setVisible(true);
        });
    }

    private static void mostrarErroNumeroGuiches() {
        JOptionPane.showMessageDialog(null,
                "O número de guichês deve ser de 1 a 20!",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
    }

    private static void mostrarErroFormatoInvalido() {
        JOptionPane.showMessageDialog(null,
                "Por favor, digite um número válido!",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
    }
}
