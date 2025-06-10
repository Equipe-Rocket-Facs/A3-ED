package com.equiperocket.projects;

import com.equiperocket.projects.avltree.AVLTree;
import com.equiperocket.projects.avltree.AVLTreeVisualizerGUI;

public class MainAVLTreeGUI {
    public static void main(String[] args) {
        AVLTree tree = new AVLTree();
        new AVLTreeVisualizerGUI(tree);
    }
}
