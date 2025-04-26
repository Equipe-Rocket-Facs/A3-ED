package org.example.projects.avltree;

public class AVLTree implements Tree<Integer, AVLTree.Node> {

    private Node root;
    private int rotationsCount;

    public AVLTree() {
        root = null;
        rotationsCount = 0;
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
        if (isUnbalancePositive(balance)) {
            int rightBalance = Node.balanceFactor(node.right);
            if (!isBalanceFactorPositive(rightBalance)) {
                // Rotation necessary to allow balance (RL -> Right Rotation and Left Rotation)
                node.right = rotateRight(node.right);
            }
            node = rotateLeft(node);
        } else if (isUnbalanceNegative(balance)) {
            int leftBalance = Node.balanceFactor(node.left);
            if (!isBalanceFactorNegative(leftBalance)) {
                // Rotation necessary to allow balance (LR -> Left Rotation and Right Rotation)
                node.left = rotateLeft(node.left);
            }
            node = rotateRight(node);
        }
        return node;
    }

    @Override
    public void insert(Integer value) {
        rotationsCount = 0;
        root = insertRecursive(root, value);
        System.out.printf("Rotations made in insertion: %d%n", rotationsCount);
    }

    private Node insertRecursive(Node node, Integer value) {
        if (isNodeEmpty(node)) return new Node(value);

        if (isValueLessThanNode(node, value)) {
            node.left = insertRecursive(node.left, value);
        } else if (isValueGreaterThanNode(node, value)) {
            node.right = insertRecursive(node.right, value);
        } else {
            return node; // Number already exists
        }
        return rebalance(node);
    }

    @Override
    public Node find(Integer value) {
        return findRecursive(root, value);
    }

    private Node findRecursive(Node node, Integer value) {
        if (isNodeEmpty(node)) return null;
        if (isValueEqualToNode(node, value)) return node;

        return isValueLessThanNode(node, value)
                ? findRecursive(node.left, value)
                : findRecursive(node.right, value);
    }

    @Override
    public void remove(Integer value) {
        rotationsCount = 0;
        root = deleteRecursive(root, value);
        System.out.printf("Rotations made in removal: %d%n", rotationsCount);
    }

    private Node deleteRecursive(Node node, Integer value) {
        if (isNodeEmpty(node)) return null;

        if (isValueLessThanNode(node, value)) {
            node.left = deleteRecursive(node.left, value);
        } else if (isValueGreaterThanNode(node, value)) {
            node.right = deleteRecursive(node.right, value);
        } else {
            if (isNodeEmpty(node.left) || isNodeEmpty(node.right)) {
                node = isNodeEmpty(node.left) ? node.right : node.left;
            } else {
                node = reorganizeSubTree(node); // In case of 2 children
            }
        }
        node = !isNodeEmpty(node) ? rebalance(node) : null;
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
        return isNodeEmpty(node.left) ? node : mostLeftChild(node.left);
    }

    @Override
    public String traverseInOrder() {
        StringBuilder sb = new StringBuilder();
        sb = traverseInOrder(root, sb);
        return sb.toString();
    }

    private StringBuilder traverseInOrder(Node node, StringBuilder sb) {
        if (!isNodeEmpty(node)) {
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
        if (!isNodeEmpty(node)) {
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
        if (!isNodeEmpty(node)) {
            traversePostOrder(node.left, sb);
            traversePostOrder(node.right, sb);
            sb.append(String.format("%d ", node.value));
        }
        return sb;
    }

    private boolean isUnbalancePositive(int balance) {
        return balance > 1;
    }

    private boolean isUnbalanceNegative(int balance) {
        return balance < -1;
    }

    private boolean isBalanceFactorPositive(int balance) {
        return balance >= 0;
    }

    private boolean isBalanceFactorNegative(int balance) {
        return balance <= 0;
    }

    private boolean isNodeEmpty(Node node) {
        return node == null;
    }

    private boolean isValueEqualToNode(Node node, Integer value) {
        return value.equals(node.value);
    }

    private boolean isValueLessThanNode(Node node, Integer value) {
        return value < node.value;
    }

    private boolean isValueGreaterThanNode(Node node, Integer value) {
        return value > node.value;
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

        private static int height(Node node) {
            return node != null ? node.height : -1;
        }

        public static void updateHeight(Node node) {
            node.height = Math.max(height(node.left), height(node.right)) + 1;
        }

        public static int balanceFactor(Node node) {
            return height(node.right) - height(node.left);
        }
    }
}
