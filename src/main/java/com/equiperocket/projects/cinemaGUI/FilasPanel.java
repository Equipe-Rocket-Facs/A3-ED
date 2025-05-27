package com.equiperocket.projects.cinemaGUI;


import com.equiperocket.projects.cinema.Cinema;
import com.equiperocket.projects.cinema.Fila;
import com.equiperocket.projects.cinema.Cliente;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilasPanel extends JPanel {
    private final Cinema cinema;
    private final Map<Integer, JScrollPane> scrollPanesMap = new HashMap<>();
    private final Map<Integer, Integer> ultimaQuantidadePorFila = new HashMap<>();

    public FilasPanel(Cinema cinema) {
        this.cinema = cinema;
        setLayout(new BorderLayout());
        setBackground(UIUtils.COR_FUNDO);
        atualizar();
        List<Fila<Cliente>> filas = cinema.getFilas();
        for (int i = 0; i < filas.size(); i++) {
            ultimaQuantidadePorFila.put(i, filas.get(i).tamanho());
        }
    }

    public void atualizarSeNecessario() {
        List<Fila<Cliente>> filas = cinema.getFilas();
        boolean precisaAtualizar = false;

        for (int i = 0; i < filas.size(); i++) {
            int tamanhoAtual = filas.get(i).tamanho();
            Integer tamanhoAnterior = ultimaQuantidadePorFila.get(i);
            if (tamanhoAnterior == null || tamanhoAtual != tamanhoAnterior) {
                precisaAtualizar = true;
                break;
            }
        }

        if (precisaAtualizar) {
            atualizar();
            for (int i = 0; i < filas.size(); i++) {
                ultimaQuantidadePorFila.put(i, filas.get(i).tamanho());
            }
        }
    }

    private void atualizar() {
        Map<Integer, Integer> scrollValues = new HashMap<>();
        for (Map.Entry<Integer, JScrollPane> entry : scrollPanesMap.entrySet()) {
            JScrollPane scrollPane = entry.getValue();
            if (scrollPane != null) {
                scrollValues.put(entry.getKey(), scrollPane.getVerticalScrollBar().getValue());
            }
        }

        removeAll();
        scrollPanesMap.clear();

        List<Fila<Cliente>> filas = cinema.getFilas();
        JPanel todasFilasPanel = new JPanel(new GridLayout(1, filas.size(), 15, 0));
        todasFilasPanel.setBackground(UIUtils.COR_FUNDO);

        for (int i = 0; i < filas.size(); i++) {
            JPanel filaPanel = criaFila(cinema, i);
            todasFilasPanel.add(filaPanel);
        }

        add(todasFilasPanel, BorderLayout.CENTER);
        add(new LegendaPanel(), BorderLayout.SOUTH);

        revalidate();
        repaint();

        for (Map.Entry<Integer, Integer> entry : scrollValues.entrySet()) {
            JScrollPane scrollPane = scrollPanesMap.get(entry.getKey());
            if (scrollPane != null) {
                final int value = entry.getValue();
                SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(value));
            }
        }
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

        JScrollPane scrollPane = new JScrollPane(clientesPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(UIUtils.COR_FUNDO);
        scrollPanesMap.put(numeroFila, scrollPane);

        filaPanel.add(tituloLabel, BorderLayout.NORTH);
        filaPanel.add(scrollPane, BorderLayout.CENTER);
        filaPanel.add(contadorLabel, BorderLayout.SOUTH);

        return filaPanel;
    }
}