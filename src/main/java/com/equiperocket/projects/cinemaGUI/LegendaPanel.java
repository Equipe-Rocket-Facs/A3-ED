package com.equiperocket.projects.cinemaGUI;

import javax.swing.*;
import java.awt.*;

public class LegendaPanel extends JPanel {
    public LegendaPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        setBackground(UIUtils.COR_FUNDO);
        addLegendaItem(UIUtils.COR_CLIENTE_IDOSO, "Preferencial");
        addLegendaItem(UIUtils.COR_CLIENTE_ESTUDANTE, "Estudante");
        addLegendaItem(UIUtils.COR_CLIENTE_NORMAL, "Normal");
    }

    private void addLegendaItem(Color cor, String texto) {
        JPanel itemLegenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        itemLegenda.setBackground(UIUtils.COR_FUNDO);
        JPanel corBox = new JPanel();
        corBox.setBackground(cor);
        corBox.setPreferredSize(new Dimension(20, 20));
        JLabel label = new JLabel(texto);
        label.setForeground(UIUtils.COR_TEXTO);
        itemLegenda.add(corBox);
        itemLegenda.add(label);
        add(itemLegenda);
    }
}
