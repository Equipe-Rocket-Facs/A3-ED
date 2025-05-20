package com.equiperocket.projects;

import com.equiperocket.projects.avltree.AVLTree;
import com.equiperocket.projects.avltree.AVLTreeVisualizerGUI;

public class MainAVLTreeGUI {
    public static void main(String[] args) {
        AVLTree tree = new AVLTree();
        AVLTreeVisualizerGUI visualizer = new AVLTreeVisualizerGUI(tree);
    }
}
