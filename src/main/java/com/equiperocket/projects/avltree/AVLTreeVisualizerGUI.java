package com.equiperocket.projects.avltree;

import com.formdev.flatlaf.FlatLightLaf;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AVLTreeVisualizerGUI {
    private final AVLTree tree;
    private JFrame frame;
    private TreePanel treePanel;
    private JTextField inputField;
    private JTextArea outputArea;
    private JTabbedPane tabbedPane;

    public AVLTreeVisualizerGUI(AVLTree tree) {
        this.tree = tree;
        initializeUI();
    }

    private void initializeUI() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        frame = new JFrame("AVL Tree Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        treePanel = new TreePanel();
        tabbedPane.addTab("Árvore", treePanel);

        // Aba de estatísticas
        tabbedPane.addTab("Estatísticas", createStatsPanel());

        frame.add(tabbedPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        inputField = new JTextField(10);
        buttonPanel.add(inputField);

        JButton insertButton = new JButton("Inserir");
        insertButton.addActionListener(e -> insertValue());
        buttonPanel.add(insertButton);

        JButton findButton = new JButton("Buscar");
        findButton.addActionListener(e -> findValue());
        buttonPanel.add(findButton);

        JButton removeButton = new JButton("Remover");
        removeButton.addActionListener(e -> removeValue());
        buttonPanel.add(removeButton);

        JButton printInOrderButton = new JButton("In-Order");
        printInOrderButton.addActionListener(e -> printInOrder());
        buttonPanel.add(printInOrderButton);

        JButton printPreOrderButton = new JButton("Pre-Order");
        printPreOrderButton.addActionListener(e -> printPreOrder());
        buttonPanel.add(printPreOrderButton);

        JButton printPostOrderButton = new JButton("Post-Order");
        printPostOrderButton.addActionListener(e -> printPostOrder());
        buttonPanel.add(printPostOrderButton);

        JButton resetButton = new JButton("Resetar");
        resetButton.addActionListener(e -> reset());
        buttonPanel.add(resetButton);

        // Área de saída com scroll
        outputArea = new JTextArea(7, 80);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Painel inferior combinando botões e área de texto
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Atualiza estatísticas ao trocar de aba
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                tabbedPane.setComponentAt(1, createStatsPanel());
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void reset() {
        frame.dispose();
        AVLTree newTree = new AVLTree();
        new AVLTreeVisualizerGUI(newTree);
    }

    private void insertValue() {
        try {
            int value = Integer.parseInt(inputField.getText());
            String rotations = tree.insert(value);
            outputArea.append("Inserido: " + value + (rotations.isEmpty() ? "" : " (" + rotations.trim() + ")") + "\n");
            inputField.setText("");
            update();
        } catch (NumberFormatException e) {
            outputArea.append("Entrada inválida! Digite um número inteiro.\n");
            inputField.setText("");
        } catch (RuntimeException e) {
            outputArea.append("Valor já está na árvore.\n");
            inputField.setText("");
        }
    }

    private void findValue() {
        try {
            int value = Integer.parseInt(inputField.getText());
            AVLTree.Node node = tree.find(value);
            if (node != null) {
                outputArea.append("Encontrado: " + node + "\n");
            } else {
                outputArea.append("Valor " + value + " não encontrado.\n");
            }
            inputField.setText("");
        } catch (NumberFormatException e) {
            outputArea.append("Entrada inválida! Digite um número inteiro.\n");
            inputField.setText("");
        }
    }

    private void removeValue() {
        try {
            int value = Integer.parseInt(inputField.getText());
            String rotations = tree.remove(value);
            outputArea.append("Removido: " + value + (rotations.isEmpty() ? "" : " (" + rotations.trim() + ")") + "\n");
            inputField.setText("");
            update();
        } catch (NumberFormatException e) {
            outputArea.append("Entrada inválida! Digite um número inteiro.\n");
            inputField.setText("");
        } catch (RuntimeException e) {
            outputArea.append("Valor não encontrado na árvore.\n");
            inputField.setText("");
        }
    }

    private void printInOrder() {
        outputArea.append("In-Order: " + tree.traverseInOrder() + "\n");
        inputField.setText("");
    }

    private void printPreOrder() {
        outputArea.append("Pre-Order: " + tree.traversePreOrder() + "\n");
        inputField.setText("");
    }

    private void printPostOrder() {
        outputArea.append("Post-Order: " + tree.traversePostOrder() + "\n");
        inputField.setText("");
    }

    private void update() {
        treePanel.repaint();
        // Atualiza estatísticas se a aba estiver aberta
        if (tabbedPane.getSelectedIndex() == 1) {
            tabbedPane.setComponentAt(1, createStatsPanel());
        }
    }

    private JPanel createStatsPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        AVLTree.Node root = tree.getRoot();
        int height = (root != null) ? treePanel.getTreeHeight(root) : 0;
        int nodes = (root != null) ? countNodes(root) : 0;

        dataset.addValue(height, "Árvore", "Altura");
        dataset.addValue(nodes, "Árvore", "Nós");

        JFreeChart chart = ChartFactory.createBarChart(
                "Estatísticas da Árvore AVL",
                "Métrica",
                "Valor",
                dataset
        );
        return new ChartPanel(chart);
    }

    private int countNodes(AVLTree.Node node) {
        if (node == null) return 0;
        return 1 + countNodes(node.getLeft()) + countNodes(node.getRight());
    }

    /**
     * Painel customizado para desenhar a árvore AVL.
     */
    private class TreePanel extends JPanel {
        private static final int NODE_DIAMETER = 40;
        private static final int VERTICAL_GAP = 70;
        private static final int INITIAL_X_OFFSET = 500;
        private static final int INITIAL_Y_OFFSET = 50;

        public TreePanel() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (tree != null) {
                AVLTree.Node root = tree.getRoot();
                if (root != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int treeHeight = getTreeHeight(root);
                    int horizontalGap = calculateHorizontalGap(treeHeight);

                    drawTree(g2d, root, INITIAL_X_OFFSET, INITIAL_Y_OFFSET, horizontalGap);
                }
            }
        }

        private void drawTree(Graphics2D g2d, AVLTree.Node node, int x, int y, int horizontalOffset) {
            if (node == null) return;

            // Desenha o nó
            g2d.setColor(new Color(0x90caf9));
            g2d.fillOval(x - NODE_DIAMETER / 2, y - NODE_DIAMETER / 2, NODE_DIAMETER, NODE_DIAMETER);
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawOval(x - NODE_DIAMETER / 2, y - NODE_DIAMETER / 2, NODE_DIAMETER, NODE_DIAMETER);

            // Valor do nó
            String value = String.valueOf(node.getValue());
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(value);
            int textHeight = fm.getHeight();
            g2d.setColor(Color.BLACK);
            g2d.drawString(value, x - textWidth / 2, y + textHeight / 4);

            // Posição dos filhos
            int nextY = y + VERTICAL_GAP;
            int leftX = x - horizontalOffset;
            int rightX = x + horizontalOffset;

            // Ligações e recursão
            if (node.getLeft() != null) {
                g2d.setColor(new Color(0x90caf9));
                g2d.drawLine(x, y + NODE_DIAMETER / 2 - 5, leftX, nextY - NODE_DIAMETER / 2 + 5);
                drawTree(g2d, node.getLeft(), leftX, nextY, horizontalOffset / 2);
            }

            if (node.getRight() != null) {
                g2d.setColor(new Color(0x90caf9));
                g2d.drawLine(x, y + NODE_DIAMETER / 2 - 5, rightX, nextY - NODE_DIAMETER / 2 + 5);
                drawTree(g2d, node.getRight(), rightX, nextY, horizontalOffset / 2);
            }
        }

        private int getTreeHeight(AVLTree.Node node) {
            if (node == null) return 0;
            return 1 + Math.max(getTreeHeight(node.getLeft()), getTreeHeight(node.getRight()));
        }

        private int calculateHorizontalGap(int height) {
            return Math.max(120, 400 / (height + 1));
        }
    }
}
