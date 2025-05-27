package com.equiperocket.projects.cinemaGUI;

import com.equiperocket.projects.cinema.Cinema;

import javax.swing.*;
import java.awt.*;

public class GuichesPanel extends JPanel {
    public GuichesPanel(Cinema cinema, JLabel statusLabel, Runnable atualizarFilas) {
        int guichesPorLinha = 4;
        int numGuiches = cinema.getNumeroGuiches();
        setLayout(new GridLayout((int) Math.ceil(numGuiches / (double)guichesPorLinha), guichesPorLinha, 10, 10));
        setBackground(UIUtils.COR_FUNDO);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int i = 0; i < numGuiches; i++) {
            add(criaGuiche(cinema, i, statusLabel, atualizarFilas));
        }
    }

    private JPanel criaGuiche(Cinema cinema, int guicheId, JLabel statusLabel, Runnable atualizarFilas) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setBackground(UIUtils.COR_FUNDO);

        JLabel labelGuiche = new JLabel("Guichê " + (guicheId + 1));
        labelGuiche.setForeground(Color.WHITE);
        labelGuiche.setFont(UIUtils.FONTE_NEGRITO);

        JButton pauseButton = UIUtils.criarBotaoEstilizado("Pausar");
        pauseButton.setPreferredSize(new Dimension(100, 30));
        pauseButton.addActionListener(e -> {
            boolean estaPausando = pauseButton.getText().equals("Pausar");
            try {
                if (estaPausando) {
                    cinema.pausarGuiche(guicheId);
                    pauseButton.setText("Retomar");
                    pauseButton.setBackground(UIUtils.COR_BOTAO_PARAR);
                    statusLabel.setText("Guichê " + (guicheId + 1) + " pausado");
                } else {
                    cinema.reativarGuiche(guicheId);
                    pauseButton.setText("Pausar");
                    pauseButton.setBackground(UIUtils.COR_BOTAO_RETOMAR);
                    statusLabel.setText("Guichê " + (guicheId + 1) + " retomado");
                }
                statusLabel.setForeground(Color.WHITE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
            atualizarFilas.run();
        });

        panel.add(labelGuiche);
        panel.add(pauseButton);
        return panel;
    }
}
