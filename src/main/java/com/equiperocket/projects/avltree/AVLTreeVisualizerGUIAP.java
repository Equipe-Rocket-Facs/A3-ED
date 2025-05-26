package com.equiperocket.projects.avltree;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Stack;

public class AVLTreeVisualizerGUIAP {
    private final AVLTree tree;
    private JFrame frame;
    private TreePanel treePanel;
    private JTextField inputField;
    private JTextArea outputArea;
    private JTabbedPane tabbedPane;
    private Color accentColor = new Color(0x1E88E5);
    private final Color accentNodeShadow = new Color(0x5C6BC0);
    private int lastAccessed = Integer.MIN_VALUE;
    private JPanel headerPanel;
    private String currentTheme = "light";
    private final Stack<Runnable> undoStack = new Stack<>();

    public AVLTreeVisualizerGUIAP(AVLTree tree) {
        this.tree = tree;
        setTheme(currentTheme); // padrão: light
        initializeUI();
    }

    private void setTheme(String theme) {
        try {
            if (theme.equals("dark")) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                accentColor = new Color(0x90caf9);
            } else {
                UIManager.setLookAndFeel(new FlatIntelliJLaf());
                accentColor = new Color(0x1E88E5);
            }
            currentTheme = theme;
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf theme");
        }
    }

    private void initializeUI() {
        frame = new JFrame("AVL Tree Visualization 🚀");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1040, 650));
        frame.setLayout(new BorderLayout());

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(accentColor);
        headerPanel.setBorder(new EmptyBorder(12, 18, 8, 18));

        JLabel logo = new JLabel("AVLTree Visualizer", JLabel.LEFT);
        logo.setFont(new Font("JetBrains Mono", Font.BOLD, 32));
        logo.setForeground(Color.white);

        JButton themeBtn = new JButton("🌙");
        themeBtn.setToolTipText("Alternar tema (claro/escuro)");
        themeBtn.setFocusPainted(false);
        themeBtn.setBackground(Color.white);
        themeBtn.addActionListener(e -> {
            if (currentTheme.equals("dark")) setTheme("light");
            else setTheme("dark");
            SwingUtilities.updateComponentTreeUI(frame);
        });

        JButton helpBtn = new JButton("❓");
        helpBtn.setToolTipText("Ajuda sobre comandos e atalhos");
        helpBtn.setFocusPainted(false);
        helpBtn.setBackground(Color.white);
        helpBtn.addActionListener(e -> showHelpDialog());

        JPanel btnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBox.setOpaque(false);
        btnBox.add(themeBtn);
        btnBox.add(helpBtn);

        headerPanel.add(logo, BorderLayout.WEST);
        headerPanel.add(btnBox, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();

        treePanel = new TreePanel();

        tabbedPane.addTab("🌳 Árvore", treePanel);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_A);

        tabbedPane.addTab("📊 Estatísticas", createStatsPanel());
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_E);

        frame.add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(new EmptyBorder(12, 0, 8, 0));
        Font btnFont = new Font(Font.SANS_SERIF, Font.BOLD, 17);

        inputField = new JTextField(7);
        inputField.setFont(btnFont.deriveFont(18f));
        inputField.setToolTipText("Digite o número aqui...");
        buttonPanel.add(inputField);

        JButton insertBtn = createButton("➕ Inserir", btnFont, accentColor, e -> insertValue());
        insertBtn.setToolTipText("Inserir valor (Enter também funciona)");
        buttonPanel.add(insertBtn);

        JButton findBtn = createButton("🔍 Buscar", btnFont, accentColor, e -> findValue());
        findBtn.setToolTipText("Buscar valor na árvore");
        buttonPanel.add(findBtn);

        JButton removeBtn = createButton("➖ Remover", btnFont, accentColor, e -> removeValue());
        removeBtn.setToolTipText("Remover valor");
        buttonPanel.add(removeBtn);

        JButton undoBtn = createButton("↩ Desfazer", btnFont, accentColor, e -> undoOperation());
        undoBtn.setToolTipText("Desfazer última operação");
        buttonPanel.add(undoBtn);

        JButton inOrderBtn = createButton("L⮞R", btnFont, accentColor, e -> printInOrder());
        inOrderBtn.setToolTipText("Percurso In-Order (LNR)");
        buttonPanel.add(inOrderBtn);

        JButton preOrderBtn = createButton("N⮞L⮞R", btnFont, accentColor, e -> printPreOrder());
        preOrderBtn.setToolTipText("Percurso Pre-Order (NLR)");
        buttonPanel.add(preOrderBtn);

        JButton postOrderBtn = createButton("L⮞R⮞N", btnFont, accentColor, e -> printPostOrder());
        postOrderBtn.setToolTipText("Percurso Post-Order (LRN)");
        buttonPanel.add(postOrderBtn);

        JButton resetBtn = createButton("🔄 Resetar", btnFont, accentColor, e -> reset(true));
        resetBtn.setToolTipText("Limpar toda a árvore");
        buttonPanel.add(resetBtn);

        outputArea = new JTextArea(7, 85);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setBackground(new Color(235, 242, 250));
        outputArea.setForeground(Color.DARK_GRAY);

        JScrollPane scrollPane = new JScrollPane(outputArea);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(0, 16, 0, 16));
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        inputField.addActionListener(e -> insertValue());
        inputField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                inputField.setBackground(Color.white);
            }
        });

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                tabbedPane.setComponentAt(1, createStatsPanel());
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JButton createButton(String text, Font font, Color color, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(font);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        btn.addActionListener(listener);
        return btn;
    }

    private void showHelpDialog() {
        JOptionPane.showMessageDialog(frame,
                """
                        🛈 AVL Visualizer ajuda\s
                        
                        ➕ Inserir: Digite um inteiro e pressione Inserir ou Enter.
                        🔍 Buscar: Busca visual o valor na árvore.
                        ➖ Remover: Remove valor informado da árvore.
                        ↩ Desfazer: Volta a última operação (inserção ou remoção)
                        Resetar: Limpa toda a árvore.
                        O nó recentemente alterado/buscado ficará destacado.
                        
                        Atalhos: ENTER=Inserir, TAB=Abas, ALT+Letra=Acesso rápido, Mouse wheel=zoom estatísticas.
                        
                        Alternar tema: botão no topo direito.
                        """,
                "Ajuda", JOptionPane.INFORMATION_MESSAGE);
    }

    private void reset(boolean ask) {
        if (ask) {
            int ok = JOptionPane.showConfirmDialog(frame,
                    "Deseja realmente limpar toda a árvore?",
                    "Confirmar Reset", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE
            );
            if (ok != JOptionPane.OK_OPTION) return;
        }
        frame.dispose();
        new AVLTreeVisualizerGUI(new AVLTree());
    }

    private void insertValue() {
        String text = inputField.getText().trim();
        try {
            int value = Integer.parseInt(text);
            String rotations = tree.insert(value);
            outputArea.append("Inserido: " + value + (rotations.isEmpty() ? "" : " [" + rotations.trim() + "]") + "\n");
            inputField.setText("");
            lastAccessed = value;
            undoStack.push(() -> { try { tree.remove(value); } catch (Exception ignored) {} });
            Toolkit.getDefaultToolkit().beep();
            update();
        } catch (NumberFormatException e) {
            showInputError("Entrada inválida! Digite um número inteiro.");
        } catch (RuntimeException e) {
            showInputError("Valor já está na árvore.");
        }
    }

    private void findValue() {
        String text = inputField.getText().trim();
        try {
            int value = Integer.parseInt(text);
            AVLTree.Node node = tree.find(value);
            lastAccessed = value;
            if (node != null) {
                outputArea.append("Encontrado: " + node + "\n");
            } else {
                outputArea.append("Valor " + value + " não encontrado.\n");
            }
            Toolkit.getDefaultToolkit().beep();
            inputField.setText("");
            update();
        } catch (NumberFormatException e) {
            showInputError("Entrada inválida! Digite um número inteiro.");
        }
    }

    private void removeValue() {
        String text = inputField.getText().trim();
        try {
            int value = Integer.parseInt(text);
            String rotations = tree.remove(value);
            outputArea.append("Removido: " + value + (rotations.isEmpty() ? "" : " [" + rotations.trim() + "]") + "\n");
            inputField.setText("");
            lastAccessed = value;
            undoStack.push(() -> { try { tree.insert(value); } catch (Exception ignored) {} });
            Toolkit.getDefaultToolkit().beep();
            update();
        } catch (NumberFormatException e) {
            showInputError("Entrada inválida! Digite um número inteiro.");
        } catch (RuntimeException e) {
            showInputError("Valor não encontrado na árvore.");
        }
    }

    private void printInOrder() {
        outputArea.append("In-Order: " + tree.traverseInOrder() + "\n");
        Toolkit.getDefaultToolkit().beep();
        inputField.setText("");
    }

    private void printPreOrder() {
        outputArea.append("Pre-Order: " + tree.traversePreOrder() + "\n");
        Toolkit.getDefaultToolkit().beep();
        inputField.setText("");
    }

    private void printPostOrder() {
        outputArea.append("Post-Order: " + tree.traversePostOrder() + "\n");
        Toolkit.getDefaultToolkit().beep();
        inputField.setText("");
    }

    private void update() {
        treePanel.fadeIn();
        if (tabbedPane.getSelectedIndex() == 1) {
            tabbedPane.setComponentAt(1, createStatsPanel());
        }
    }


    private void undoOperation() {
        if (!undoStack.isEmpty()) {
            undoStack.pop().run();
            outputArea.append("Desfazer: última operação revertida.\n");
            Toolkit.getDefaultToolkit().beep();
            update();
        } else {
            outputArea.append("Nada a desfazer.\n");
        }
    }

    private void showInputError(String msg) {
        outputArea.append(msg + "\n");
        inputField.setBackground(new Color(255, 210, 210));
        Toolkit.getDefaultToolkit().beep();
        inputField.selectAll();
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
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        return chartPanel;
    }

    private int countNodes(AVLTree.Node node) {
        if (node == null) return 0;
        return 1 + countNodes(node.getLeft()) + countNodes(node.getRight());
    }

    /** --- Painel customizado, com animação e informações extras --- */
    private class TreePanel extends JPanel {
        private static final int NODE_DIAMETER = 50; // Tamanho do nó
        private static final int VERTICAL_GAP = 100; // Espaço vertical entre os nós
        private static final int HORIZONTAL_SPACING = 20; // Espaço horizontal entre os nós
        private float alpha = 1.0f;


        private double zoom = 1.0;
        private int offsetX = 0, offsetY = 0, lastMouseX, lastMouseY;

        private final Map<AVLTree.Node, Point> nodePositions = new HashMap<>(); // Mapa para armazenar posições dos nós

        public TreePanel() {
            setBackground(new Color(250, 251, 253));
            setToolTipText(""); // ativa as tooltips nativas

            addMouseWheelListener(e -> {
                double scale = e.getPreciseWheelRotation() < 0 ? 1.1 : 0.9;
                zoom *= scale;
                zoom = Math.max(zoom, 0.2); // limita zoom mínimo
                repaint();
            });

            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
                public void mouseReleased(MouseEvent e) {
                    setCursor(Cursor.getDefaultCursor());
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                public void mouseDragged(MouseEvent e) {
                    offsetX += e.getX() - lastMouseX;
                    offsetY += e.getY() - lastMouseY;
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                    repaint();
                }
            });
        }

        private int calculateNodePositions(AVLTree.Node node, int depth, int x) {
            if (node == null) return x;

            x = calculateNodePositions(node.getLeft(), depth + 1, x);

            int nodeX = x;
            int nodeY = depth * VERTICAL_GAP;
            nodePositions.put(node, new Point(nodeX, nodeY));

            x += NODE_DIAMETER + HORIZONTAL_SPACING;

            x = calculateNodePositions(node.getRight(), depth + 1, x);

            return x;
        }

        public void fadeIn() {
            alpha = 0.15f;
            Timer timer = new Timer(13, null);
            timer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    alpha += 0.09f;
                    if (alpha >= 1f) {
                        alpha = 1f;
                        timer.stop();
                    }
                    repaint();
                }
            });
            timer.start();
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            g2d.translate(offsetX, offsetY);
            g2d.scale(zoom, zoom);

            nodePositions.clear();
            if (tree.getRoot() != null) {
                calculateNodePositions(tree.getRoot(), 0, 0);
                drawPrecomputedTree(g2d, tree.getRoot());
            }

            g2d.dispose();
        }

        private void drawPrecomputedTree(Graphics2D g2d, AVLTree.Node node) {
            if (node == null) return;

            Point point = nodePositions.get(node);
            int x = point.x;
            int y = point.y;

            drawNode(g2d, node, x, y);

            if (node.getLeft() != null) {
                Point left = nodePositions.get(node.getLeft());
                drawLine(g2d, x, y, left.x, left.y);
                drawPrecomputedTree(g2d, node.getLeft());
            }

            if (node.getRight() != null) {
                Point right = nodePositions.get(node.getRight());
                drawLine(g2d, x, y, right.x, right.y);
                drawPrecomputedTree(g2d, node.getRight());
            }
        }

        private void drawNode(Graphics2D g2d, AVLTree.Node node, int x, int y) {
            g2d.setColor(accentNodeShadow);
            g2d.fillOval(x - NODE_DIAMETER / 2 + 3, y - NODE_DIAMETER / 2 + 8, NODE_DIAMETER, NODE_DIAMETER);

            if (node.getValue() == lastAccessed)
                g2d.setColor(new Color(0xfbc02d));
            else
                g2d.setColor(accentColor);

            g2d.fillOval(x - NODE_DIAMETER / 2, y - NODE_DIAMETER / 2, NODE_DIAMETER, NODE_DIAMETER);

            g2d.setColor(Color.DARK_GRAY);
            g2d.drawOval(x - NODE_DIAMETER / 2, y - NODE_DIAMETER / 2, NODE_DIAMETER, NODE_DIAMETER);

            String value = String.valueOf(node.getValue());
            FontMetrics fm = g2d.getFontMetrics(new Font(Font.MONOSPACED, Font.BOLD, 14));
            int textWidth = fm.stringWidth(value);
            int textHeight = fm.getHeight();
            g2d.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
            g2d.setColor(Color.black);
            g2d.drawString(value, x - textWidth / 2, y + textHeight / 4);

            int bal = getBalance(node);
            g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            String info = "h=" + node.getHeight() + ", bf=" + bal;
            g2d.setColor(Color.black);
            g2d.drawString(info, x - NODE_DIAMETER / 2, y + NODE_DIAMETER / 2 + 12);
        }

        private void drawLine(Graphics2D g2d, int x1, int y1, int x2, int y2) {
            g2d.setColor(accentColor.darker());
            g2d.setStroke(new BasicStroke(3f));
            g2d.drawLine(x1, y1 + NODE_DIAMETER / 3, x2, y2 - NODE_DIAMETER / 3);
        }

        private int getTreeHeight(AVLTree.Node node) {
            if (node == null) return 0;
            return 1 + Math.max(getTreeHeight(node.getLeft()), getTreeHeight(node.getRight()));
        }

        private int getBalance(AVLTree.Node n) {
            if (n == null) return 0;
            return (n.getLeft() == null ? 0 : n.getLeft().getHeight()) -
                    (n.getRight() == null ? 0 : n.getRight().getHeight());
        }
    }
}
