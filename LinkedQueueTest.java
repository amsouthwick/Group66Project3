import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for LinkedQueue implementation.
 */
public class LinkedQueueTest {
    
    private LinkedQueue<String> queue;

    @BeforeEach
    public void setUp() {
        queue = new LinkedQueue<>();
    }

    @Test
    public void testEnqueueNormal() {
        // Enqueue A, B, C: size 3, peek() is A.
        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");
        assertEquals(3, queue.size(), "Size should be 3 after 3 enqueues");
        assertEquals("A", queue.peek(), "Front of queue should be A");
    }

    @Test
    public void testEnqueueNull() {
        // enqueue(null): IllegalArgumentException, size unchanged.
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            queue.enqueue(null);
        });
        assertEquals("Item cannot be null", exception.getMessage());
        assertEquals(0, queue.size(), "Size should remain 0");
    }

    @Test
    public void testDequeueNormal() {
        // Returns A, then B; size 1. (Assuming start with A, B, C)
        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");
        
        assertEquals("A", queue.dequeue());
        assertEquals("B", queue.dequeue());
        assertEquals(1, queue.size(), "Size should be 1 after 2 dequeues");
    }

    @Test
    public void testDequeueEmpty() {
        // Empty queue: NoSuchElementException.
        assertThrows(NoSuchElementException.class, () -> {
            queue.dequeue();
        });
    }

    @Test
    public void testPeekNormal() {
        // Returns A; size stays 3.
        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");
        
        assertEquals("A", queue.peek());
        assertEquals(3, queue.size(), "Size should not change after peek");
    }

    @Test
    public void testPeekEmpty() {
        // Empty queue: NoSuchElementException.
        assertThrows(NoSuchElementException.class, () -> {
            queue.peek();
        });
    }

    @Test
    public void testIsEmpty() {
        // False after one enqueue. Emptied queue returns true (boundary).
        assertTrue(queue.isEmpty(), "New queue should be empty");
        
        queue.enqueue("A");
        assertFalse(queue.isEmpty(), "Should not be empty after enqueue");
        
        queue.dequeue();
        assertTrue(queue.isEmpty(), "Should be empty after removing only item");
    }
}