package com.equiperocket.projects.avltree;

/**
 * A generic tree interface that defines basic operations for a tree data structure.
 *
 * @param <T> The type of values used as keys in the tree
 * @param <E> The type of elements stored in the tree
 */
public interface Tree<T, E> {

    /**
     * Inserts a new value into the tree.
     *
     * @param value The value to insert
     * @return
     */
    String insert(T value);

    /**
     * Finds and returns an element in the tree based on the given value.
     *
     * @param value The value to search for
     * @return The element associated with the value if found
     */
    E find(T value);

    /**
     * Removes a value and its associated element from the tree.
     *
     * @param value The value to remove
     * @return
     */
    String remove(T value);

    /**
     * Performs an in-order traversal of the tree.
     *
     * @return A string representation of the tree in in-order traversal
     */
    String traverseInOrder();

    /**
     * Performs a pre-order traversal of the tree.
     *
     * @return A string representation of the tree in pre-order traversal
     */
    String traversePreOrder();

    /**
     * Performs a post-order traversal of the tree.
     *
     * @return A string representation of the tree in post-order traversal
     */
    String traversePostOrder();
}
