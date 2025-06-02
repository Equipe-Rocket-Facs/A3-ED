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
     * If the value already exists, an exception is thrown.
     *
     * @param value The value to insert
     * @return A string representation of the inserted element
     * @throws RuntimeException If the value already exists in the tree
     */
    String insert(T value) throws RuntimeException;

    /**
     * Finds and returns an element in the tree based on the given value.
     *
     * @param value The value to search for
     * @return The element associated with the value if found
     */
    E find(T value);

    /**
     * Removes a value and its associated element from the tree.
     * If the value is not found, an exception is thrown.
     *
     * @param value The value to remove
     * @return A string representation of the removed element
     * @throws RuntimeException If the value is not found in the tree
     */
    String remove(T value) throws RuntimeException;

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
