package com.equiperocket.projects.avltree;

public class AVLTree implements Tree<Integer, AVLTree.Node> {

    private Node root;
    private int rotationsCount;

    public AVLTree() {
        root = null;
        rotationsCount = 0;
    }

    public Node getRoot() {
        return root;
    }

    // Left element of root now becomes root
    private Node rotateRight(Node node) {
        Node left = node.left;
        node.left = left.right;
        left.right = node;

        Node.updateHeight(node);
        Node.updateHeight(left);

        rotationsCount++;
        return left;
    }

    // Right element of root now becomes root
    private Node rotateLeft(Node node) {
        Node right = node.right;
        node.right = right.left;
        right.left = node;

        Node.updateHeight(node);
        Node.updateHeight(right);

        rotationsCount++;
        return right;
    }

    private Node rebalance(Node node) {
        Node.updateHeight(node);
        int balance = Node.balanceFactor(node);
        // Positive unbalance in root tree (right has more levels than left)
        // and not positive (un)balance in right subtree requires double rotation.
        // The same happens with negative unbalance in root tree. If the left
        // subtree doesn't have a negative (un)balance too, we rotate two times.
        if (balance > 1) {
            if (Node.balanceFactor(node.right) < 0) {
                // Rotation necessary to allow balance (RL -> Right Rotation and Left Rotation)
                node.right = rotateRight(node.right);
            }
            node = rotateLeft(node);
        } else if (balance < -1) {
            if (Node.balanceFactor(node.left) > 0) {
                // Rotation necessary to allow balance (LR -> Left Rotation and Right Rotation)
                node.left = rotateLeft(node.left);
            }
            node = rotateRight(node);
        }
        return node;
    }

    @Override
    public String insert(Integer value) throws RuntimeException {
        rotationsCount = 0;
        root = insertRecursive(root, value);
        return "Rotations made in insertion: " + rotationsCount;
    }

    private Node insertRecursive(Node node, Integer value) {
        if (node == null) return new Node(value);

        if (value < node.value) {
            node.left = insertRecursive(node.left, value);
        } else if (value > node.value) {
            node.right = insertRecursive(node.right, value);
        } else {
            throw new RuntimeException(String.format("Value %d already exists in the tree!", value));
        }

        return rebalance(node);
    }

    @Override
    public Node find(Integer value) {
        return findRecursive(root, value);
    }

    private Node findRecursive(Node node, Integer value) {
        if (node == null) return null;
        if (value.equals(node.value)) return node;

        return value < node.value
                ? findRecursive(node.left, value)
                : findRecursive(node.right, value);
    }

    @Override
    public String remove(Integer value) throws RuntimeException {
        rotationsCount = 0;
        root = deleteRecursive(root, value);
        return "Rotations made in removal: " + rotationsCount;
    }

    private Node deleteRecursive(Node node, Integer value) {
        if (node == null) {
            throw new RuntimeException(String.format("Value %d not found in the tree!", value));
        }

        if (value < node.value) {
            node.left = deleteRecursive(node.left, value);
        } else if (value > node.value) {
            node.right = deleteRecursive(node.right, value);
        } else {
            if (node.left == null || node.right == null) {
                node = node.left == null ? node.right : node.left;
            } else {
                node = reorganizeSubTree(node); // In case of 2 children
            }
        }

        node = node != null ? rebalance(node) : null;
        return node;
    }

    // Take the smallest value in the RIGHT subtree to replace the removed parent.
    // Another option would be to pick the biggest value int the left subtree.
    // After the process we clean the right/left subtree by removing the old child.
    private Node reorganizeSubTree(Node root) {
        Integer smallestValue = mostLeftChild(root.right).value;
        root.value = smallestValue;
        root.right = deleteRecursive(root.right, smallestValue);
        return root;
    }

    private Node mostLeftChild(Node node) {
        return node.left == null ? node : mostLeftChild(node.left);
    }

    @Override
    public String traverseInOrder() {
        StringBuilder sb = new StringBuilder();
        sb = traverseInOrder(root, sb);
        return sb.toString();
    }

    private StringBuilder traverseInOrder(Node node, StringBuilder sb) {
        if (node != null) {
            traverseInOrder(node.left, sb);
            sb.append(String.format("%d ", node.value));
            traverseInOrder(node.right, sb);
        }
        return sb;
    }

    @Override
    public String traversePreOrder() {
        StringBuilder sb = new StringBuilder();
        sb = traversePreOrder(root, sb);
        return sb.toString();
    }

    private StringBuilder traversePreOrder(Node node, StringBuilder sb) {
        if (node != null) {
            sb.append(String.format("%d ", node.value));
            traversePreOrder(node.left, sb);
            traversePreOrder(node.right, sb);
        }
        return sb;
    }

    @Override
    public String traversePostOrder() {
        StringBuilder sb = new StringBuilder();
        sb = traversePostOrder(root, sb);
        return sb.toString();
    }

    private StringBuilder traversePostOrder(Node node, StringBuilder sb) {
        if (node != null) {
            traversePostOrder(node.left, sb);
            traversePostOrder(node.right, sb);
            sb.append(String.format("%d ", node.value));
        }
        return sb;
    }

    protected class Node {
        private Integer value;
        private Integer height;
        private Node left;
        private Node right;

        public Node(Integer value) {
            this.value = value;
            height = 0;
            left = right = null;
        }

        public Integer getValue() {
            return value;
        }

        public Integer getHeight() {
            return height;
        }

        public Node getLeft() {
            return left;
        }

        public Node getRight() {
            return right;
        }

        private static int height(Node node) {
            return node != null ? node.height : -1;
        }

        public static void updateHeight(Node node) {
            node.height = Math.max(height(node.left), height(node.right)) + 1;
        }

        public static int balanceFactor(Node node) {
            return height(node.right) - height(node.left);
        }

        @Override
        public String toString() {
            return "Node of value " + value + " at height " + height;
        }
    }
}
