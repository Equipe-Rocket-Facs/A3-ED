package com.equiperocket.projects.avltree;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Stack;

public class AVLTreeVisualizerGUI {
    private final AVLTree tree;
    private JFrame frame;
    private TreePanel treePanel;
    private JTextField inputField;
    private JTextArea outputArea;
    private JTabbedPane tabbedPane;
    private Color accentColor = new Color(0x1E88E5);
    private int lastAccessed = Integer.MIN_VALUE;
    private JPanel headerPanel;
    private String currentTheme = "light";
    private final Stack<Runnable> undoStack = new Stack<>();

    public AVLTreeVisualizerGUI(AVLTree tree) {
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

        // Removed theme toggle button as requested

        JButton helpBtn = new JButton("❓");
        helpBtn.setToolTipText("Ajuda sobre comandos e atalhos");
        helpBtn.setFocusPainted(false);
        helpBtn.setBackground(Color.white);
        helpBtn.addActionListener(e -> showHelpDialog());

        JPanel btnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBox.setOpaque(false);
        btnBox.add(helpBtn);

        headerPanel.add(logo, BorderLayout.WEST);
        headerPanel.add(btnBox, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();

        treePanel = new TreePanel();

        tabbedPane.addTab("🌳 Árvore", treePanel);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_A);

        frame.add(tabbedPane, BorderLayout.CENTER);

        // WrapLayout for button panel for responsiveness
        JPanel buttonPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 8));
        buttonPanel.setBorder(new EmptyBorder(12, 0, 8, 0));
        Font btnFont = new Font(Font.SANS_SERIF, Font.BOLD, 17);

        inputField = new JTextField(7);
        inputField.setFont(btnFont.deriveFont(18f));
        inputField.setToolTipText("Digite o número aqui...");
        inputField.setMaximumSize(new Dimension(150, 40));
        inputField.setPreferredSize(new Dimension(150, 40));
        buttonPanel.add(inputField);

        JButton insertBtn = createButton("➕ Inserir", btnFont, accentColor, e -> insertValue());
        insertBtn.setToolTipText("Inserir valor (Enter também funciona)");
        insertBtn.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(insertBtn);

        JButton findBtn = createButton("🔍 Buscar", btnFont, accentColor, e -> findValue());
        findBtn.setToolTipText("Buscar valor na árvore");
        findBtn.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(findBtn);

        // Increased width to prevent truncation
        JButton removeBtn = createButton("➖ Remover", btnFont, accentColor, e -> removeValue());
        removeBtn.setToolTipText("Remover valor");
        removeBtn.setPreferredSize(new Dimension(150, 40));  // Increased width for full text display
        buttonPanel.add(removeBtn);

        JButton undoBtn = createButton("↩ Desfazer", btnFont, accentColor, e -> undoOperation());
        undoBtn.setToolTipText("Desfazer última operação");
        undoBtn.setPreferredSize(new Dimension(130, 40));
        buttonPanel.add(undoBtn);

        JButton inOrderBtn = createButton("In Order", btnFont, accentColor, e -> printInOrder());
        inOrderBtn.setToolTipText("Percurso In-Order (LNR)");
        inOrderBtn.setPreferredSize(new Dimension(150, 40));
        buttonPanel.add(inOrderBtn);

        JButton preOrderBtn = createButton("Pre Order", btnFont, accentColor, e -> printPreOrder());
        preOrderBtn.setToolTipText("Percurso Pre-Order (NLR)");
        preOrderBtn.setPreferredSize(new Dimension(150, 40));
        buttonPanel.add(preOrderBtn);

        JButton postOrderBtn = createButton("Pos Ordem", btnFont, accentColor, e -> printPostOrder());
        postOrderBtn.setToolTipText("Percurso Post-Order (LRN)");
        postOrderBtn.setPreferredSize(new Dimension(150, 40));
        buttonPanel.add(postOrderBtn);

        JButton resetBtn = createButton("🔄 Resetar", btnFont, accentColor, e -> reset(true));
        resetBtn.setToolTipText("Limpar toda a árvore");
        resetBtn.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(resetBtn);

        outputArea = new JTextArea(7, 85);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setBackground(new Color(235, 242, 250));
        outputArea.setForeground(new Color(107, 114, 128)); // neutral gray #6b7280

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

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JButton createButton(String text, Font font, Color color, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(font);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        // Subtle rounded corners for modern look
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(7, 16, 7, 16)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Smooth color transition on hover
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });
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
                        
                        Atalhos: ENTER=Inserir.
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
        treePanel.centerTree(); // center the visualization always on update
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

    /** --- Custom panel with animation and extra info --- */
    private class TreePanel extends JPanel {
        private static final int NODE_DIAMETER = 50; // Node size
        private static final int VERTICAL_GAP = 100; // Vertical spacing between nodes
        private static final int HORIZONTAL_SPACING = 20; // Horizontal spacing between nodes
        private float alpha = 1.0f;

        private double zoom = 1.0;
        private int offsetX = 0, offsetY = 0, lastMouseX, lastMouseY;

        private final Map<AVLTree.Node, Point> nodePositions = new HashMap<>(); // Store node positions

        public TreePanel() {
            setBackground(Color.white); // light background as per guidelines
            setToolTipText(""); // enables native tooltips

            addMouseWheelListener(e -> {
                double scale = e.getPreciseWheelRotation() < 0 ? 1.1 : 0.9;
                zoom *= scale;
                zoom = Math.max(zoom, 0.2); // minimum zoom
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

        // New method to calculate tree bounding box width and height
        private Dimension getTreeDimensions() {
            if (nodePositions.isEmpty()) return new Dimension(0, 0);
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;
            for (Point p : nodePositions.values()) {
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }
            // Node diameter adds to total width/height
            int width = maxX - minX + NODE_DIAMETER;
            int height = maxY - minY + NODE_DIAMETER;
            return new Dimension(width, height);
        }

        // New method to center tree visualization in panel
        public void centerTree() {
            if (tree.getRoot() == null) {
                offsetX = getWidth() / 2;
                offsetY = NODE_DIAMETER; // some padding from top
                repaint();
                return;
            }
            calculateNodePositions(tree.getRoot(), 0, 0);
            Dimension treeDim = getTreeDimensions();
            // Center horizontally
            offsetX = (getWidth() - (int)(treeDim.width * zoom)) / 2;
            // Center vertically with some padding
            offsetY = (getHeight() - (int)(treeDim.height * zoom)) / 2;
            if (offsetY < NODE_DIAMETER)
                offsetY = NODE_DIAMETER;
            repaint();
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
        }

        private void drawLine(Graphics2D g2d, int x1, int y1, int x2, int y2) {
            g2d.setColor(accentColor.darker());
            g2d.setStroke(new BasicStroke(3f));
            g2d.drawLine(x1, y1 + NODE_DIAMETER / 3, x2, y2 - NODE_DIAMETER / 3);
        }
    }

    /**
     * WrapLayout: a FlowLayout subclass that supports wrapping in rows, useful for responsive button layout.
     * Source inspired/adapted from: https://tips4java.wordpress.com/2008/11/06/wrap-layout/
     */
    public static class WrapLayout extends FlowLayout
    {
        private Dimension preferredLayoutSize;

        public WrapLayout()
        {
            super();
        }

        public WrapLayout(int align)
        {
            super(align);
        }

        public WrapLayout(int align, int hgap, int vgap)
        {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target)
        {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target)
        {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }

        private Dimension layoutSize(Container target, boolean preferred)
        {
            synchronized (target.getTreeLock())
            {
                int targetWidth = target.getWidth();

                if (targetWidth == 0)
                    targetWidth = Integer.MAX_VALUE;

                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
                int maxWidth = targetWidth - horizontalInsetsAndGap;

                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                int nmembers = target.getComponentCount();

                for (int i = 0; i < nmembers; i++)
                {
                    Component m = target.getComponent(i);

                    if (m.isVisible())
                    {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                        if (rowWidth + d.width > maxWidth)
                        {
                            addRow(dim, rowWidth, rowHeight);
                            rowWidth = 0;
                            rowHeight = 0;
                        }

                        if (rowWidth != 0)
                        {
                            rowWidth += hgap;
                        }

                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }

                addRow(dim, rowWidth, rowHeight);

                dim.width += horizontalInsetsAndGap;
                dim.height += insets.top + insets.bottom + vgap * 2;

                // When using a scroll pane or the DecoratedLookAndFeel we need to
                // make sure the preferred size is less than the size of the
                // target containter so shrinking works correctly.
                Container scrollPane = SwingUtilities.getUnwrappedParent(target);
                if (scrollPane instanceof JScrollPane)
                {
                    JScrollPane jsp = (JScrollPane) scrollPane;
                    Insets scrollInsets = jsp.getInsets();
                    dim.width -= (scrollInsets.left + scrollInsets.right);
                }

                return dim;
            }
        }

        private void addRow(Dimension dim, int rowWidth, int rowHeight)
        {
            dim.width = Math.max(dim.width, rowWidth);

            if (dim.height > 0)
            {
                dim.height += getVgap();
            }

            dim.height += rowHeight;
        }
    }
}
