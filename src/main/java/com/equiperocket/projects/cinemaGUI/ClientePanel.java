package com.equiperocket.projects.cinemaGUI;

import com.equiperocket.projects.cinema.Cliente;
import com.equiperocket.projects.cinema.TipoClient;
import javax.swing.*;
import java.awt.*;

public class ClientePanel extends JPanel {
    public ClientePanel(Cliente cliente) {
        setLayout(new BorderLayout());
        Color corCliente = switch (cliente.getTipo()) {
            case IDOSO -> UIUtils.COR_CLIENTE_IDOSO;
            case ESTUDANTE -> UIUtils.COR_CLIENTE_ESTUDANTE;
            default -> UIUtils.COR_CLIENTE_NORMAL;
        };
        setBackground(corCliente);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(corCliente);
        JLabel clienteLabel = new JLabel("Cliente #" + cliente.getId(), JLabel.LEFT);
        JLabel tipoLabel = new JLabel("Tipo: " + cliente.getTipo(), JLabel.LEFT);
        clienteLabel.setForeground(Color.WHITE);
        tipoLabel.setForeground(Color.WHITE);
        infoPanel.add(clienteLabel);
        infoPanel.add(tipoLabel);

        add(infoPanel, BorderLayout.CENTER);

        if (cliente.getTipo() == TipoClient.IDOSO) {
            JLabel priorityLabel = new JLabel("★", JLabel.CENTER);
            priorityLabel.setForeground(Color.WHITE);
            priorityLabel.setFont(UIUtils.FONTE_TITULO);
            add(priorityLabel, BorderLayout.EAST);
        }
    }
}
