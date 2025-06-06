package com.equiperocket.projects.cinemaGUI;

import com.equiperocket.projects.cinema.Cinema;
import javax.swing.*;
import java.awt.*;

public class EstatisticasDialog extends JDialog {
    public EstatisticasDialog(JFrame parent, Cinema cinema) {
        super(parent, "Estatísticas", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        JPanel statsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        statsPanel.setBackground(UIUtils.COR_FUNDO);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        try {
            addEstatistica(statsPanel, "Total de clientes atendidos: ", String.valueOf(cinema.getTotalClientesAtendidos()));
            addEstatistica(statsPanel, "Clientes na fila: ", String.valueOf(cinema.getTotalClientesNasFilas()));
        } catch (Exception e) {
            JLabel erroLabel = new JLabel("Erro ao carregar estatísticas: " + e.getMessage());
            erroLabel.setForeground(Color.RED);
            statsPanel.add(erroLabel);
        }
        add(statsPanel);
    }
    private void addEstatistica(JPanel panel, String label, String valor) {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linha.setBackground(UIUtils.COR_FUNDO);
        JLabel labelComp = new JLabel(label);
        JLabel valorComp = new JLabel(valor);
        labelComp.setForeground(UIUtils.COR_TEXTO);
        valorComp.setForeground(UIUtils.COR_SECUNDARIA);
        linha.add(labelComp);
        linha.add(valorComp);
        panel.add(linha);
    }
}
