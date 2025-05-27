package com.equiperocket.projects.cinemaGUI;

import com.equiperocket.projects.cinema.Cinema;
import com.equiperocket.projects.cinema.Cliente;
import com.equiperocket.projects.cinema.TipoClient;
import javax.swing.*;
import java.awt.*;

public class AdicionarClienteDialog extends JDialog {
    public AdicionarClienteDialog(JFrame parent, Cinema cinema, JLabel statusLabel, Runnable atualizarFilas) {
        super(parent, "Adicionar Cliente", true);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UIUtils.COR_FUNDO);

        JPanel inputPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        inputPanel.setBackground(UIUtils.COR_FUNDO);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel quantidadeLabel = new JLabel("Quantidade de Clientes:");
        quantidadeLabel.setForeground(UIUtils.COR_TEXTO);
        inputPanel.add(quantidadeLabel);

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 200, 1);
        JSpinner quantidadeSpinner = new JSpinner(spinnerModel);
        quantidadeSpinner.setEditor(new JSpinner.NumberEditor(quantidadeSpinner, "#"));
        inputPanel.add(quantidadeSpinner);

        JLabel tipoLabel = new JLabel("Tipo de Cliente:");
        tipoLabel.setForeground(UIUtils.COR_TEXTO);
        inputPanel.add(tipoLabel);

        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"NORMAL", "IDOSO", "ESTUDANTE", "RANDOM"});
        tipoCombo.setBackground(UIUtils.COR_SECUNDARIA);
        tipoCombo.setForeground(Color.BLACK);
        tipoCombo.setFont(UIUtils.FONTE_PADRAO);
        inputPanel.add(tipoCombo);

        JButton confirmarBtn = UIUtils.criarBotaoEstilizado("Confirmar");
        confirmarBtn.addActionListener(e -> {
            try {
                String tipoSelecionado = (String) tipoCombo.getSelectedItem();
                int quantidade = (Integer) quantidadeSpinner.getValue();
                for (int i = 0; i < quantidade; i++) {
                    Cliente novoCliente;
                    if ("RANDOM".equals(tipoSelecionado)) {
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
                atualizarFilas.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao adicionar cliente", JOptionPane.ERROR_MESSAGE);
            }
        });
        inputPanel.add(confirmarBtn);

        add(inputPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(parent);
    }
}
