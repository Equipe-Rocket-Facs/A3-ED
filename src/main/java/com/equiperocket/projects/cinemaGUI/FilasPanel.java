package com.equiperocket.projects.cinemaGUI;

import com.equiperocket.projects.cinema.Cinema;
import com.equiperocket.projects.cinema.Fila;
import com.equiperocket.projects.cinema.Cliente;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FilasPanel extends JPanel {
    private final Cinema cinema;

    public FilasPanel(Cinema cinema) {
        this.cinema = cinema;
        setLayout(new BorderLayout());
        setBackground(UIUtils.COR_FUNDO);
        atualizar();
    }

    public void atualizar() {
        removeAll();
        List<Fila<Cliente>> filas = cinema.getFilas();
        JPanel todasFilasPanel = new JPanel(new GridLayout(1, filas.size(), 15, 0));
        todasFilasPanel.setBackground(UIUtils.COR_FUNDO);

        for (int i = 0; i < filas.size(); i++) {
            todasFilasPanel.add(criaFila(cinema, i));
        }

        add(todasFilasPanel, BorderLayout.CENTER);
        add(new LegendaPanel(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private JPanel criaFila(Cinema cinema, int numeroFila) {
        Fila<Cliente> fila = cinema.getFila(numeroFila);
        JPanel filaPanel = new JPanel(new BorderLayout(5, 5));
        filaPanel.setBackground(UIUtils.COR_FUNDO);
        filaPanel.setBorder(BorderFactory.createLineBorder(UIUtils.COR_SECUNDARIA, 2));

        JLabel tituloLabel = new JLabel("Fila " + (numeroFila + 1), JLabel.CENTER);
        tituloLabel.setForeground(UIUtils.COR_SECUNDARIA);
        tituloLabel.setFont(UIUtils.FONTE_TITULO);
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel clientesPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        clientesPanel.setBackground(UIUtils.COR_FUNDO);
        cinema.getGuiche(numeroFila).ordenarFilaPorPrioridade();
        for (Cliente cliente : fila) {
            clientesPanel.add(new ClientePanel(cliente));
        }

        JLabel contadorLabel = new JLabel("Pessoas na fila: " + fila.tamanho(), JLabel.CENTER);
        contadorLabel.setForeground(UIUtils.COR_TEXTO);
        contadorLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        filaPanel.add(tituloLabel, BorderLayout.NORTH);
        filaPanel.add(new JScrollPane(clientesPanel), BorderLayout.CENTER);
        filaPanel.add(contadorLabel, BorderLayout.SOUTH);

        return filaPanel;
    }
}
