package com.equiperocket.projects;

import com.equiperocket.projects.avltree.AVLTree;
import com.equiperocket.projects.avltree.AVLTreeVisualizer;
import com.equiperocket.projects.avltree.Tree;

import java.util.Scanner;

public class MainAVLTree {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        processMenuOperations();
    }

    private static void processMenuOperations() {
        AVLTree tree = new AVLTree();
        AVLTreeVisualizer visualizer = new AVLTreeVisualizer(tree);
        visualizer.show();

        do {
            System.out.println("1. Insert");
            System.out.println("2. Find");
            System.out.println("3. Remove");
            System.out.println("4. Print");
            System.out.println("5. Reset");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int option = readInteger();

            switch (option) {
                case 1:
                    System.out.print("Enter the value to insert: ");
                    tree.insert(readInteger());
                    visualizer.update();
                    break;
                case 2:
                    System.out.print("Enter the value to find: ");
                    var node = tree.find(readInteger());
                    System.out.println(node);
                    break;
                case 3:
                    System.out.print("Enter the value to remove: ");
                    tree.remove(readInteger());
                    visualizer.update();
                    break;
                case 4:
                    processPrintMenu(tree);
                    break;
                case 5:
                    tree = new AVLTree();
                    visualizer = new AVLTreeVisualizer(tree);
                    visualizer.show();
                    break;
                case 6:
                    sc.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid option!");
            }
        } while (true);
    }

    private static void processPrintMenu(Tree tree) {
        do {
            System.out.println("1. Traverse in order");
            System.out.println("2. Traverse pre order");
            System.out.println("3. Traverse post order");
            System.out.println("4. Return");
            System.out.print("Choose an option: ");
            int option = readInteger();

            switch (option) {
                case 1:
                    System.out.println(tree.traverseInOrder());
                    break;
                case 2:
                    System.out.println(tree.traversePreOrder());
                    break;
                case 3:
                    System.out.println(tree.traversePostOrder());
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        } while (true);
    }

    private static int readInteger() {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.print("Invalid input! Please enter an integer: ");
            return readInteger();
        }
    }
}