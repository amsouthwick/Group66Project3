import java.util.NoSuchElementException;

/**
 * A generic singly linked queue used to manage the game's turn order
 * and the deck of cards.
 *
 * @param <E> the type of elements held in this queue
 */
public class LinkedQueue<E> {

    /**
     * Private static nested class representing a node in the queue.
     *
     * @param <T> the type of data stored in the node
     */
    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size;

    /**
     * Constructs an empty LinkedQueue.
     */
    public LinkedQueue() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Adds an item to the back of the queue.
     *
     * @param item the element to be added to the queue
     * @throws IllegalArgumentException if the item is null
     */
    public void enqueue(E item) {
        if (item == null) throw new IllegalArgumentException("Item cannot be null");
        Node<E> newNode = new Node<>(item);
        if (isEmpty()) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        size++;
    }

    /**
     * Removes and returns the item at the front of the queue.
     *
     * @return the element removed from the front of the queue
     * @throws NoSuchElementException if the queue is empty
     */
    public E dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        E item = head.data;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return item;
    }

    /**
     * Returns the item at the front of the queue without removing it.
     *
     * @return the element at the front of the queue
     * @throws NoSuchElementException if the queue is empty
     */
    public E peek() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        return head.data;
    }

    /**
     * Checks if the queue is currently empty.
     *
     * @return true if the queue has no elements, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements currently in the queue.
     *
     * @return the size of the queue
     */
    public int size() {
        return size;
    }
}