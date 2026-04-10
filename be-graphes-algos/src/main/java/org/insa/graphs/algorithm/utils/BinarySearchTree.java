package org.insa.graphs.algorithm.utils;

import java.util.*;

public class BinarySearchTree<E extends Comparable<E>> implements PriorityQueue<E> {

    // Underlying implementation

    // Elements, sorted by their value
    private SortedSet<E> sortedSet;

    // Elements, by their hashcode.
    private HashSet<E> elems;

    /**
     * Create a new empty binary search tree.
     */
    public BinarySearchTree() {
        this.sortedSet = new TreeSet<>();
        this.elems = new HashSet<>();
    }

    /**
     * Create a copy of the given binary search tree.
     *
     * @param bst Binary search tree to copy.
     */
    public BinarySearchTree(BinarySearchTree<E> bst) {
        this.sortedSet = new TreeSet<>(bst.sortedSet);
        this.elems = new HashSet<>(bst.elems);
    }

    @Override
    public boolean isEmpty() {
        return this.sortedSet.isEmpty();
    }

    @Override
    public int size() {
        return this.sortedSet.size();
    }

    @Override
    public void insert(E x) {
        this.sortedSet.add(x);
        this.elems.add(x);
    }

    @Override
    public void remove(E x) throws ElementNotFoundException {

        if (this.elems.contains(x)) {
            // x is known to be here.

            boolean removed = this.sortedSet.remove(x) ;
            this.elems.remove(x) ;

            if (!removed) {
                // The element x was not found in the sorted tree, because its value has changed.
                // However, we know it is here.

                // This forces the sorted set to be reorganized
                SortedSet<E> ts = this.sortedSet ;
                this.sortedSet = new TreeSet<>() ;
                for (E y : ts) {
                    this.sortedSet.add(y) ;
                }

                removed = this.sortedSet.remove(x) ;
                assert(removed) ;
            }
        }
        else {
            throw new ElementNotFoundException(x);
        }
    }

    @Override
    public E findMin() throws EmptyPriorityQueueException {
        if (isEmpty()) {
            throw new EmptyPriorityQueueException();
        }
        return sortedSet.first();
    }

    @Override
    public E deleteMin() throws EmptyPriorityQueueException {
        E min = findMin();
        remove(min);
        return min;
    }

}
