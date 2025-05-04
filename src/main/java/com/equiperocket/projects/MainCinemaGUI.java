package com.equiperocket.projects;

import com.equiperocket.projects.cinema.Cinema;
import com.equiperocket.projects.cinema.Cliente;
import com.equiperocket.projects.cinema.Fila;
import com.equiperocket.projects.cinema.TipoClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Queue;

public class MainCinemaGUI extends JFrame {
    // Core components
    private final Cinema cinema;
    private JPanel filasPanel;
    private JLabel statusLabel;
    private JButton atendimentoBtn;

    // State
    private boolean atendimentoAutomaticoAtivo = false;

    // UI Colors
    private static final Color COR_FUNDO = new Color(40, 44, 52);
    private static final Color COR_SECUNDARIA = new Color(97, 175, 239);
    private static final Color COR_TEXTO = new Color(220, 223, 228);
    private static final Color COR_CLIENTE_IDOSO = new Color(184, 134, 11);
    private static final Color COR_CLIENTE_ESTUDANTE = new Color(51, 122, 183);
    private static final Color COR_CLIENTE_NORMAL = new Color(70, 136, 71);
    private static final Color COR_BOTAO_PARAR = new Color(255, 99, 71);
    private static final Color COR_BOTAO_RETOMAR = new Color(153, 217, 234);

    // UI Constants
    private static final int LARGURA_JANELA = 1200;
    private static final int ALTURA_JANELA = 800;
    private static final int INTERVALO_ATUALIZACAO = 1000;
    private static final int GUICHES_POR_LINHA = 4; // Mantemos este valor para organização visual
    private static final Dimension TAMANHO_SCROLL_GUICHES = new Dimension(900, 200); // Aumentado a altura para acomodar mais linhas
    private static final Dimension TAMANHO_BOTAO_GUICHE = new Dimension(100, 30);
    private static final Dimension TAMANHO_INDICADOR_LEGENDA = new Dimension(20, 20);
    private static final Font FONTE_PADRAO = new Font("Arial", Font.PLAIN, 14);
    private static final Font FONTE_NEGRITO = new Font("Arial", Font.BOLD, 14);
    private static final Font FONTE_TITULO = new Font("Arial", Font.BOLD, 16);

    public MainCinemaGUI(Cinema cinema) {
        this.cinema = cinema;
        configurarJanela();
        iniciarComponentes();
        iniciarTimerAtualizacao();
    }

    private void configurarJanela() {
        setTitle("Sistema de Cinema");
        setSize(LARGURA_JANELA, ALTURA_JANELA);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COR_FUNDO);
    }

    private void iniciarComponentes() {
        JPanel mainPanel = criarPainelPrincipal();
        JPanel topPanel = criarPainelSuperior();
        JPanel guichesPanel = criarPainelGuiches();

        inicializarStatusLabel(); // Inicializa statusLabel primeiro
        inicializarPainelFilas(); // Depois inicializa as filas que podem usar statusLabel

        topPanel.add(guichesPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(filasPanel, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel criarPainelPrincipal() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(COR_FUNDO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return mainPanel;
    }

    private void inicializarPainelFilas() {
        filasPanel = new JPanel();
        filasPanel.setLayout(new BoxLayout(filasPanel, BoxLayout.X_AXIS));
        filasPanel.setBackground(COR_FUNDO);
        atualizarFilasVisuais();
    }

    private void inicializarStatusLabel() {
        statusLabel = new JLabel("Sistema em funcionamento", JLabel.CENTER);
        estilizarComponente(statusLabel);
    }

    private JPanel criarPainelSuperior() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        headerPanel.setBackground(COR_FUNDO);

        JButton addClienteBtn = criarBotaoEstilizado("Adicionar Cliente");
        atendimentoBtn = criarBotaoEstilizado("Iniciar Atendimento");
        JButton estatisticasBtn = criarBotaoEstilizado("Estatísticas");

        configurarAcoesDosBotoes(addClienteBtn, atendimentoBtn, estatisticasBtn);
        adicionarBotoesAoPainel(headerPanel, addClienteBtn, atendimentoBtn, estatisticasBtn);

        return headerPanel;
    }

    private void configurarAcoesDosBotoes(JButton addClienteBtn, JButton atendimentoBtn, JButton estatisticasBtn) {
        addClienteBtn.addActionListener(e -> mostrarDialogoAdicionarCliente());
        atendimentoBtn.addActionListener(e -> toggleAtendimentoAutomatico());
        estatisticasBtn.addActionListener(e -> mostrarEstatisticas());
    }

    private void adicionarBotoesAoPainel(JPanel panel, JButton... botoes) {
        for (JButton botao : botoes) {
            panel.add(botao);
        }
    }

    private JButton criarBotaoEstilizado(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(COR_SECUNDARIA);
        botao.setForeground(Color.BLACK);
        botao.setFocusPainted(false);
        botao.setFont(FONTE_NEGRITO);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        configurarEfeitosMouseBotao(botao);

        return botao;
    }

    private void configurarEfeitosMouseBotao(JButton botao) {
        botao.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(COR_SECUNDARIA.brighter());
            }

            public void mouseExited(MouseEvent e) {
                botao.setBackground(COR_SECUNDARIA);
            }
        });
    }

    private void toggleAtendimentoAutomatico() {
        atendimentoAutomaticoAtivo = !atendimentoAutomaticoAtivo;
        atualizarEstadoAtendimento();
    }

    private void atualizarEstadoAtendimento() {
        if (atendimentoAutomaticoAtivo) {
            ativarAtendimentoAutomatico();
        } else {
            desativarAtendimentoAutomatico();
        }
    }

    private void ativarAtendimentoAutomatico() {
        atendimentoBtn.setText("Parar Atendimento");
        atendimentoBtn.setBackground(COR_BOTAO_PARAR);
        iniciarAtendimentoAutomatico();
        statusLabel.setText("Atendimento automático iniciado");
    }

    private void desativarAtendimentoAutomatico() {
        atendimentoBtn.setText("Iniciar Atendimento");
        atendimentoBtn.setBackground(COR_SECUNDARIA);
        pararAtendimentoAutomatico();
        statusLabel.setText("Atendimento automático parado");
    }

    private void iniciarAtendimentoAutomatico() {
        try {
            int tempo = requisitarTempoAtendimento();
            cinema.iniciarAtendimentoAutomatico(tempo);
            atualizarFilasVisuais();
            statusLabel.setForeground(Color.WHITE);
        } catch (Exception e) {
            mostrarErro("Erro ao iniciar atendimento automático", e.getMessage());
            atendimentoAutomaticoAtivo = false;
            atendimentoBtn.setText("Iniciar Atendimento");
            atendimentoBtn.setBackground(COR_SECUNDARIA);
        }
    }

    private int requisitarTempoAtendimento(){
        try {
            String input = JOptionPane.showInputDialog(this,
                    "Digite o intervalo de tempo em segundos para o atendimento automático (1-60):",
                    "Configurar Atendimento",
                    JOptionPane.QUESTION_MESSAGE);

            int tempo = Integer.parseInt(input);
            if (tempo < 1 || tempo > 60) {
                throw new IllegalArgumentException("O tempo deve estar entre 1 e 60 segundos");
            }
            return tempo;
        } catch (NumberFormatException e) {
            mostrarErro("Erro ao iniciar atendimento automático", "Por favor, digite um número válido");
            atendimentoAutomaticoAtivo = false;
            atendimentoBtn.setText("Iniciar Atendimento");
            atendimentoBtn.setBackground(COR_SECUNDARIA);
            return -1;
        }
    }
    private void pararAtendimentoAutomatico() {
        try {
            cinema.pararAtendimentoAutomatico();
            statusLabel.setForeground(Color.WHITE);
        } catch (Exception e) {
            mostrarErro("Erro ao parar atendimento automático", e.getMessage());
            // Mantém o estado atual em caso de erro
        }
    }

    private JPanel criarPainelGuiches() {
        int numGuiches = cinema.getNumeroGuiches();
        int numLinhas = (int) Math.ceil((double) numGuiches / GUICHES_POR_LINHA);

        JPanel guichesPanel = new JPanel(new GridLayout(numLinhas, GUICHES_POR_LINHA, 10, 10));
        guichesPanel.setBackground(COR_FUNDO);
        guichesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < numGuiches; i++) {
            guichesPanel.add(criarPainelGuiche(i));
        }

        return criarPainelScrollGuiches(guichesPanel);
    }

    private JPanel criarPainelScrollGuiches(JPanel guichesPanel) {
        // Configurar o painel de rolagem para garantir que todos os guichês sejam visíveis
        JScrollPane scrollPane = new JScrollPane(guichesPanel);
        scrollPane.setPreferredSize(TAMANHO_SCROLL_GUICHES);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Garantir que a rolagem funcione corretamente
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Melhora a velocidade de rolagem
        
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(COR_FUNDO);
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);

        return wrapperPanel;
    }

    private JPanel criarPainelGuiche(int guicheId) {
        JPanel guicheContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        guicheContainer.setBackground(COR_FUNDO);

        JLabel labelGuiche = criarLabelGuiche(guicheId);
        JButton pauseButton = criarBotaoPausarGuiche(guicheId);

        guicheContainer.add(labelGuiche);
        guicheContainer.add(pauseButton);

        return guicheContainer;
    }

    private JLabel criarLabelGuiche(int guicheId) {
        JLabel labelGuiche = new JLabel("Guichê " + (guicheId + 1));
        labelGuiche.setForeground(Color.WHITE);
        labelGuiche.setFont(FONTE_NEGRITO);
        return labelGuiche;
    }

    private JButton criarBotaoPausarGuiche(int guicheId) {
        JButton pauseButton = criarBotaoEstilizado("Pausar");
        pauseButton.setPreferredSize(TAMANHO_BOTAO_GUICHE);

        pauseButton.addActionListener(e -> gerenciarEstadoGuiche(guicheId, pauseButton));

        return pauseButton;
    }

    private void gerenciarEstadoGuiche(int guicheId, JButton pauseButton) {
        boolean estaPausando = pauseButton.getText().equals("Pausar");

        if (estaPausando) {
            pausarGuiche(guicheId, pauseButton);
        } else {
            retomarGuiche(guicheId, pauseButton);
        }

        atualizarFilasVisuais();
    }

    private void pausarGuiche(int guicheId, JButton pauseButton) {
        try {
            cinema.pausarGuiche(guicheId);
            pauseButton.setText("Retomar");
            pauseButton.setBackground(COR_BOTAO_PARAR);
            statusLabel.setText("Guichê " + (guicheId + 1) + " pausado");
            statusLabel.setForeground(Color.WHITE);
        } catch (Exception e) {
            mostrarErro("Erro ao pausar guichê", e.getMessage());
            // Reverte o estado do botão caso ocorra erro
            pauseButton.setText("Pausar");
            pauseButton.setBackground(COR_SECUNDARIA);
        }
    }

    private void retomarGuiche(int guicheId, JButton pauseButton) {
        try {
            cinema.reativarGuiche(guicheId);
            pauseButton.setText("Pausar");
            pauseButton.setBackground(COR_BOTAO_RETOMAR);
            statusLabel.setText("Guichê " + (guicheId + 1) + " retomado");
            statusLabel.setForeground(Color.WHITE);
        } catch (Exception e) {
            mostrarErro("Erro ao retomar guichê", e.getMessage());
            // Reverte o estado do botão caso ocorra erro
            pauseButton.setText("Retomar");
            pauseButton.setBackground(COR_BOTAO_PARAR);
        }
    }

private void atualizarFilasVisuais() {
        try {
            filasPanel.removeAll();
            List<Fila<Cliente>> filasDoCinema = cinema.getFilas();

            JPanel todasFilasPanel = criarPainelTodasFilas(filasDoCinema);

            filasPanel.add(todasFilasPanel);
            adicionarLegenda();
            filasPanel.revalidate();
            filasPanel.repaint();
            
            // Se chegou aqui sem erros, podemos resetar a cor do statusLabel caso esteja em vermelho
            if (statusLabel.getForeground().equals(Color.RED)) {
                statusLabel.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            mostrarErro("Erro ao atualizar interface", e.getMessage());
        }
    }

    private JPanel criarPainelTodasFilas(List<Fila<Cliente>> filasDoCinema) {
        JPanel todasFilasPanel = new JPanel(new GridLayout(1, filasDoCinema.size(), 15, 0));
        todasFilasPanel.setBackground(COR_FUNDO);

        for (int numeroFila = 0; numeroFila < filasDoCinema.size(); numeroFila++) {
            todasFilasPanel.add(criarPainelFila(numeroFila));
        }

        return todasFilasPanel;
    }

    private JPanel criarPainelFila(int numeroFila) {
        Fila<Cliente> fila = cinema.getFila(numeroFila);
        JPanel painelFila = new JPanel(new BorderLayout(5, 5));

        painelFila.setBackground(COR_FUNDO);
        painelFila.setBorder(BorderFactory.createLineBorder(COR_SECUNDARIA, 2));

        adicionarComponentesFila(painelFila, numeroFila, fila);

        return painelFila;
    }

    private void adicionarComponentesFila(JPanel painelFila, int numeroFila, Fila<Cliente> fila) {
        JLabel tituloLabel = criarTituloFila(numeroFila);
        JPanel clientesPanel = criarPainelClientes(fila, numeroFila);
        JLabel contadorLabel = criarContadorFila(fila);

        painelFila.add(tituloLabel, BorderLayout.NORTH);
        painelFila.add(new JScrollPane(clientesPanel), BorderLayout.CENTER);
        painelFila.add(contadorLabel, BorderLayout.SOUTH);
    }

    private JLabel criarTituloFila(int numeroFila) {
        JLabel tituloLabel = new JLabel("Fila " + (numeroFila + 1), JLabel.CENTER);
        tituloLabel.setForeground(COR_SECUNDARIA);
        tituloLabel.setFont(FONTE_TITULO);
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return tituloLabel;
    }

    private JPanel criarPainelClientes(Fila<Cliente> fila, int numeroFila) {
        JPanel clientesPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        clientesPanel.setBackground(COR_FUNDO);

        cinema.getGuiche(numeroFila).ordenarFilaPorPrioridade();

        for (Cliente cliente : fila) {
            clientesPanel.add(criarPainelCliente(cliente));
        }

        return clientesPanel;
    }

    private JLabel criarContadorFila(Fila<Cliente> fila) {
        JLabel contadorLabel = new JLabel("Pessoas na fila: " + fila.tamanho(), JLabel.CENTER);
        contadorLabel.setForeground(COR_TEXTO);
        contadorLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return contadorLabel;
    }

    private JPanel criarPainelCliente(Cliente cliente) {
        JPanel clientePanel = new JPanel(new BorderLayout());
        Color corCliente = obterCorCliente(cliente);
        clientePanel.setBackground(corCliente);
        clientePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        adicionarInformacoesCliente(clientePanel, cliente, corCliente);

        return clientePanel;
    }

    private void adicionarInformacoesCliente(JPanel clientePanel, Cliente cliente, Color corCliente) {
        JPanel infoPanel = criarPainelInfoCliente(cliente, corCliente);
        clientePanel.add(infoPanel, BorderLayout.CENTER);

        if (cliente.getTipo() == TipoClient.IDOSO) {
            adicionarIndicadorPrioridade(clientePanel);
        }
    }

    private JPanel criarPainelInfoCliente(Cliente cliente, Color corCliente) {
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(corCliente);

        JLabel clienteLabel = new JLabel("Cliente #" + cliente.getId(), JLabel.LEFT);
        JLabel tipoLabel = new JLabel("Tipo: " + cliente.getTipo(), JLabel.LEFT);

        clienteLabel.setForeground(Color.WHITE);
        tipoLabel.setForeground(Color.WHITE);

        infoPanel.add(clienteLabel);
        infoPanel.add(tipoLabel);

        return infoPanel;
    }

    private void adicionarIndicadorPrioridade(JPanel clientePanel) {
        JLabel priorityLabel = new JLabel("★", JLabel.CENTER);
        priorityLabel.setForeground(Color.WHITE);
        priorityLabel.setFont(FONTE_TITULO);
        clientePanel.add(priorityLabel, BorderLayout.EAST);
    }

    private Color obterCorCliente(Cliente cliente) {
        return switch (cliente.getTipo()) {
            case IDOSO -> COR_CLIENTE_IDOSO;
            case ESTUDANTE -> COR_CLIENTE_ESTUDANTE;
            default -> COR_CLIENTE_NORMAL;
        };
    }

    private void adicionarLegenda() {
        JPanel legendaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legendaPanel.setBackground(COR_FUNDO);

        adicionarLegendaItem(legendaPanel, COR_CLIENTE_IDOSO, "Preferencial");
        adicionarLegendaItem(legendaPanel, COR_CLIENTE_ESTUDANTE, "Estudante");
        adicionarLegendaItem(legendaPanel, COR_CLIENTE_NORMAL, "Normal");

        filasPanel.add(legendaPanel, BorderLayout.SOUTH);
    }

    private void adicionarLegendaItem(JPanel panel, Color cor, String texto) {
        JPanel itemLegenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        itemLegenda.setBackground(COR_FUNDO);

        JPanel corBox = new JPanel();
        corBox.setBackground(cor);
        corBox.setPreferredSize(TAMANHO_INDICADOR_LEGENDA);

        JLabel label = new JLabel(texto);
        label.setForeground(COR_TEXTO);

        itemLegenda.add(corBox);
        itemLegenda.add(label);
        panel.add(itemLegenda);
    }

    private void iniciarTimerAtualizacao() {
        Timer refreshTimer = new Timer(INTERVALO_ATUALIZACAO, e -> atualizarFilasVisuais());
        refreshTimer.start();
    }

    private void mostrarDialogoAdicionarCliente() {
        try {
            JDialog dialog = criarDialogoAdicionarCliente();
            JPanel inputPanel = criarPainelInputCliente();
            
            // Campo para quantidade de clientes
            JLabel quantidadeLabel = new JLabel("Quantidade de Clientes:");
            quantidadeLabel.setForeground(COR_TEXTO);
            inputPanel.add(quantidadeLabel);
            
            SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 50, 1);
            JSpinner quantidadeSpinner = new JSpinner(spinnerModel);
            quantidadeSpinner.setEditor(new JSpinner.NumberEditor(quantidadeSpinner, "#"));
            inputPanel.add(quantidadeSpinner);
            
            // Adiciona aviso sobre o limite de clientes
            JLabel avisoLabel = new JLabel("Atenção: Máximo de 50 clientes por vez!");
            avisoLabel.setForeground(Color.YELLOW);
            avisoLabel.setFont(new Font("Arial", Font.BOLD, 12));
            inputPanel.add(avisoLabel);
            
            inputPanel.add(Box.createVerticalStrut(10));
            
            // Tipo de cliente
            JLabel tipoLabel = new JLabel("Tipo de Cliente:");
            tipoLabel.setForeground(COR_TEXTO);
            inputPanel.add(tipoLabel);
            
            JComboBox<String> tipoCombo = criarComboBoxTipoCliente();
            inputPanel.add(tipoCombo);
            
            inputPanel.add(Box.createVerticalStrut(10));
            
            JButton confirmarBtn = criarBotaoConfirmarNovoCliente(dialog, tipoCombo, quantidadeSpinner);
            inputPanel.add(confirmarBtn);

            dialog.add(inputPanel, BorderLayout.CENTER);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (Exception e) {
            mostrarErro("Erro ao exibir diálogo", e.getMessage());
        }
    }

    private JDialog criarDialogoAdicionarCliente() {
        JDialog dialog = new JDialog(this, "Adicionar Cliente", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(COR_FUNDO);
        return dialog;
    }

    private JPanel criarPainelInputCliente() {
        JPanel inputPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        inputPanel.setBackground(COR_FUNDO);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return inputPanel;
    }

    private JComboBox<String> criarComboBoxTipoCliente() {
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"NORMAL", "IDOSO", "ESTUDANTE", "RANDOM"});
        tipoCombo.setBackground(COR_SECUNDARIA);
        tipoCombo.setForeground(Color.BLACK);
        tipoCombo.setFont(FONTE_PADRAO);
        return tipoCombo;
    }

    private JButton criarBotaoConfirmarNovoCliente(JDialog dialog, JComboBox<String> tipoCombo, JSpinner quantidadeSpinner) {
        JButton confirmarBtn = criarBotaoEstilizado("Confirmar");
        confirmarBtn.addActionListener(e -> {
            try {
                String tipoSelecionado = (String) tipoCombo.getSelectedItem();
                int quantidade = (Integer) quantidadeSpinner.getValue();
                
                for (int i = 0; i < quantidade; i++) {
                    Cliente novoCliente;
                    
                    if ("RANDOM".equals(tipoSelecionado)) {
                        // Escolher um tipo aleatório entre NORMAL, IDOSO e ESTUDANTE
                        TipoClient[] tipos = {TipoClient.NORMAL, TipoClient.IDOSO, TipoClient.ESTUDANTE};
                        int indiceAleatorio = (int) (Math.random() * tipos.length);
                        novoCliente = new Cliente(tipos[indiceAleatorio]);
                    } else {
                        TipoClient tipo = TipoClient.valueOf(tipoSelecionado);
                        novoCliente = new Cliente(tipo);
                    }
                    
                    cinema.adicionarCliente(novoCliente);
                }
                
                statusLabel.setText(quantidade + " cliente(s) adicionado(s) com sucesso");
                statusLabel.setForeground(Color.WHITE);
                dialog.dispose();
            } catch (Exception ex) {
                mostrarErro("Erro ao adicionar cliente", ex.getMessage());
                // Não fecha o diálogo em caso de erro para permitir nova tentativa
            }
        });
        return confirmarBtn;
    }

    private void mostrarEstatisticas() {
        try {
            JDialog dialog = criarDialogoEstatisticas();
            JPanel statsPanel = criarPainelEstatisticas();

            adicionarEstatisticas(statsPanel);

            dialog.add(statsPanel);
            dialog.setVisible(true);
        } catch (Exception e) {
            mostrarErro("Erro ao exibir estatísticas", e.getMessage());
        }
    }

    private JDialog criarDialogoEstatisticas() {
        JDialog dialog = new JDialog(this, "Estatísticas", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    private JPanel criarPainelEstatisticas() {
        JPanel statsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        statsPanel.setBackground(COR_FUNDO);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return statsPanel;
    }

    private void adicionarEstatisticas(JPanel statsPanel) {
        try {
            adicionarEstatistica(statsPanel, "Total de clientes atendidos: ",
                String.valueOf(cinema.getTotalClientesAtendidos()));
            adicionarEstatistica(statsPanel, "Tempo médio de espera: ", "20 segundos");
            adicionarEstatistica(statsPanel, "Clientes na fila: ",
                String.valueOf(cinema.getTotalClientesNasFilas()));
        } catch (Exception e) {
            // Adiciona uma mensagem de erro ao painel de estatísticas
            JLabel erroLabel = new JLabel("Erro ao carregar estatísticas: " + e.getMessage());
            erroLabel.setForeground(Color.RED);
            statsPanel.add(erroLabel);
        }
    }

    private void adicionarEstatistica(JPanel panel, String label, String valor) {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linha.setBackground(COR_FUNDO);

        JLabel labelComp = new JLabel(label);
        JLabel valorComp = new JLabel(valor);

        labelComp.setForeground(COR_TEXTO);
        valorComp.setForeground(COR_SECUNDARIA);

        linha.add(labelComp);
        linha.add(valorComp);

        panel.add(linha);
    }

    private void estilizarComponente(JComponent componente) {
        componente.setBackground(COR_SECUNDARIA);
        componente.setForeground(Color.WHITE);
        componente.setFont(FONTE_PADRAO);
    }
    
    /**
     * Exibe uma mensagem de erro para o usuário
     * @param titulo Título da janela de erro
     * @param mensagem Mensagem de erro a ser exibida
     */
    private void mostrarErro(String titulo, String mensagem) {
        // Exibe o diálogo de erro
        JOptionPane.showMessageDialog(this,
                mensagem,
                titulo,
                JOptionPane.ERROR_MESSAGE);
                
        // Também atualiza o statusLabel para indicar o erro (se já estiver inicializado)
        if (statusLabel != null) {
            statusLabel.setText("ERRO: " + mensagem);
            statusLabel.setForeground(Color.RED);
        }
        
        // Imprime o erro no console para debug
        System.err.println(titulo + ": " + mensagem);
    }

    public static void main(String[] args) {
        configurarLookAndFeel();
        iniciarAplicacao();
    }

    private static void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void iniciarAplicacao() {
        boolean entradaValida = false;
        
        while (!entradaValida) {
            String input = solicitarNumeroGuiches();
            
            // Se o usuário cancelar o diálogo, sair do loop
            if (input == null) {
                System.exit(0);
                return;
            }
            
            // Tentar processar a entrada
            entradaValida = processarInputGuiches(input);
        }
    }

    private static String solicitarNumeroGuiches() {
        return JOptionPane.showInputDialog(null,
                "Digite o número de guichês desejado (de 1 a 9):",
                "Configuração Inicial",
                JOptionPane.QUESTION_MESSAGE);
    }

    private static boolean processarInputGuiches(String input) {
        try {
            int numeroGuiches = Integer.parseInt(input);
            if (numeroGuiches >= 1 && numeroGuiches <= 9) {
                criarEExibirGUI(numeroGuiches);
                return true; // Entrada válida, sair do loop
            } else {
                mostrarErroNumeroGuiches();
                return false; // Entrada inválida, continuar no loop
            }
        } catch (NumberFormatException e) {
            mostrarErroFormatoInvalido();
            return false; // Entrada inválida, continuar no loop
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
                "O número de guichês deve ser de 1 a 9!",
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
