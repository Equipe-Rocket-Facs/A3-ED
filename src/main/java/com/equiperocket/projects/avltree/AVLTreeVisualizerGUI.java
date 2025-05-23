package com.equiperocket.projects.avltree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A class that visualizes an AVL tree in a graphical window.
 * It displays the tree structure with nodes and connections.
 */
public class AVLTreeVisualizerGUI {
    private final AVLTree tree;
    private JFrame frame;
    private TreePanel treePanel;
    private JTextField inputField;
    private JTextArea outputArea;

    /**
     * Creates a new AVL tree visualizer for the given tree.
     * @param tree The AVL tree to visualize
     */
    public AVLTreeVisualizerGUI(AVLTree tree) {
        this.tree = tree;
        initializeUI();
    }

    /**
     * Initializes the user interface components.
     */
    private void initializeUI() {
        frame = new JFrame("AVL Tree Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());

        treePanel = new TreePanel();
        frame.add(treePanel, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout());

        inputField = new JTextField(10);
        buttonPanel.add(inputField);

        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertValue();
            }
        });
        buttonPanel.add(insertButton);

        JButton findButton = new JButton("Find");
        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findValue();
            }
        });
        buttonPanel.add(findButton);

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeValue();
            }
        });
        buttonPanel.add(removeButton);

        JButton printInOrderButton = new JButton("Print In-Order");
        printInOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printInOrder();
            }
        });
        buttonPanel.add(printInOrderButton);

        JButton printPreOrderButton = new JButton("Print Pre-Order");
        printPreOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printPreOrder();
            }
        });
        buttonPanel.add(printPreOrderButton);

        JButton printPostOrderButton = new JButton("Print Post-Order");
        printPostOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printPostOrder();
            }
        });
        buttonPanel.add(printPostOrderButton);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        buttonPanel.add(resetButton);

        // Área de saída com scroll
        outputArea = new JTextArea(8, 80); // largura maior
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

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void reset() {
        frame.dispose(); // Fecha a janela atual
        AVLTree newTree = new AVLTree(); // Cria uma nova instância da árvore
        new AVLTreeVisualizerGUI(newTree); // Inicia uma nova interface
    }

    private void insertValue() {
        try {
            int value = Integer.parseInt(inputField.getText());
            String rotations = tree.insert(value);
            outputArea.append("Inserted: " + value + ", " + rotations + "\n");
            inputField.setText("");
            update();
        } catch (NumberFormatException e) {
            outputArea.append("Invalid input! Please enter an integer.\n");
            inputField.setText("");
        }  catch (RuntimeException e) {
            outputArea.append("Value is already in the tree.\n");
            inputField.setText("");
        }
    }

    private void findValue() {
        try {
            int value = Integer.parseInt(inputField.getText());
            AVLTree.Node node = tree.find(value);
            if (node != null) {
                outputArea.append("Found: " + node + "\n");
            } else {
                outputArea.append("Value " + value + " not found.\n");
            }
            inputField.setText("");
        } catch (NumberFormatException e) {
            outputArea.append("Invalid input! Please enter an integer.\n");
            inputField.setText("");
        }

    }

    private void removeValue() {
        try {
            int value = Integer.parseInt(inputField.getText());
            String rotations = tree.remove(value);
            outputArea.append("Removed: " + value + ", " + rotations + "\n");
            inputField.setText("");
            update();
        } catch (NumberFormatException e) {
            outputArea.append("Invalid input! Please enter an integer.\n");
            inputField.setText("");
        } catch (RuntimeException e) {
            outputArea.append("Value not found in the tree.\n");
            inputField.setText("");
        }
    }

    private void printInOrder() {
        outputArea.append("In-Order Traversal: " + tree.traverseInOrder() + "\n");
        inputField.setText("");
    }

    private void printPreOrder() {
        outputArea.append("Pre-Order Traversal: " + tree.traversePreOrder() + "\n");
        inputField.setText("");
    }

    private void printPostOrder() {
        outputArea.append("Post-Order Traversal: " + tree.traversePostOrder() + "\n");
        inputField.setText("");
    }

    private void update() {
        treePanel.repaint(); // Atualiza a visualização da árvore
    }


    /**
     * A custom panel that draws the AVL tree.
     */
    private class TreePanel extends JPanel {
        private static final int NODE_DIAMETER = 40;
        private static final int VERTICAL_GAP = 70;
        private static final int INITIAL_X_OFFSET = 400;
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
                    // Use Graphics2D for better rendering
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Calculate the tree height to determine the horizontal spacing
                    int treeHeight = getTreeHeight(root);
                    int horizontalGap = calculateHorizontalGap(treeHeight);

                    // Draw the tree starting from the root
                    drawTree(g2d, root, INITIAL_X_OFFSET, INITIAL_Y_OFFSET, horizontalGap);
                }
            }
        }

        private void drawTree(Graphics2D g2d, AVLTree.Node node, int x, int y, int horizontalOffset) {
            if (node == null) return;

            // Draw the node
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillOval(x - NODE_DIAMETER / 2, y - NODE_DIAMETER / 2, NODE_DIAMETER, NODE_DIAMETER);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x - NODE_DIAMETER / 2, y - NODE_DIAMETER / 2, NODE_DIAMETER, NODE_DIAMETER);

            // Draw the node value
            String value = String.valueOf(node.getValue());
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(value);
            int textHeight = fm.getHeight();
            g2d.drawString(value, x - textWidth / 2, y + textHeight / 4);

            // Calculate positions for child nodes
            int nextY = y + VERTICAL_GAP;
            int leftX = x - horizontalOffset;
            int rightX = x + horizontalOffset;

            // Draw connections to children and then draw the children
            if (node.getLeft() != null) {
                g2d.drawLine(x, y + NODE_DIAMETER / 2 - 5, leftX, nextY - NODE_DIAMETER / 2 + 5);
                drawTree(g2d, node.getLeft(), leftX, nextY, horizontalOffset / 2);
            }

            if (node.getRight() != null) {
                g2d.drawLine(x, y + NODE_DIAMETER / 2 - 5, rightX, nextY - NODE_DIAMETER / 2 + 5);
                drawTree(g2d, node.getRight(), rightX, nextY, horizontalOffset / 2);
            }
        }

        private int getTreeHeight(AVLTree.Node node) {
            if (node == null) return 0;
            return 1 + Math.max(getTreeHeight(node.getLeft()), getTreeHeight(node.getRight()));
        }

        private int calculateHorizontalGap(int height) {
            return Math.max(150, 400 / (height + 1));
        }
    }
}
