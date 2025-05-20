package com.equiperocket.projects.avltree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A class that visualizes an AVL tree in a graphical window.
 * It displays the tree structure with nodes and connections.
 */
public class AVLTreeVisualizer {
    private final AVLTree tree;
    private JFrame frame;
    private TreePanel treePanel;

    /**
     * Creates a new AVL tree visualizer for the given tree.
     *
     * @param tree The AVL tree to visualize
     */
    public AVLTreeVisualizer(AVLTree tree) {
        this.tree = tree;
        initializeUI();
    }

    /**
     * Initializes the user interface components.
     */
    private void initializeUI() {
        frame = new JFrame("AVL Tree Visualization");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        treePanel = new TreePanel();
        frame.add(treePanel);

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);

        // Add window listener to handle window closing
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }
        });
    }

    /**
     * Shows the visualization window.
     */
    public void show() {
        frame.setVisible(true);
    }

    /**
     * Updates the tree visualization.
     */
    public void update() {
        if (treePanel != null) {
            treePanel.repaint();
        }
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

        /**
         * Recursively draws the tree.
         *
         * @param g2d              The graphics context
         * @param node             The current node to draw
         * @param x                The x-coordinate of the node
         * @param y                The y-coordinate of the node
         * @param horizontalOffset The horizontal offset for child nodes
         */
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

        /**
         * Calculates the height of the tree.
         *
         * @param node The root node
         * @return The height of the tree
         */
        private int getTreeHeight(AVLTree.Node node) {
            if (node == null) return 0;
            return 1 + Math.max(getTreeHeight(node.getLeft()), getTreeHeight(node.getRight()));
        }

        /**
         * Calculates the horizontal gap based on the tree height.
         *
         * @param height The height of the tree
         * @return The horizontal gap to use
         */
        private int calculateHorizontalGap(int height) {
            // Adjust the gap based on the tree height to ensure nodes don't overlap
            return Math.max(150, 400 / (height + 1));
        }
    }
}