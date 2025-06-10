package com.equiperocket.projects;

import com.equiperocket.projects.avltree.AVLTree;
import com.equiperocket.projects.avltree.AVLTreeVisualizerGUI;
import com.equiperocket.projects.avltree.AVLTreeVisualizerGUIAP;

public class MainAVLTreeGUI {
    public static void main(String[] args) {
        AVLTree tree = new AVLTree();
        new AVLTreeVisualizerGUIAP(tree);
    }
}
