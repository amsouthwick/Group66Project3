import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GameSnapshot implementation.
 */
public class GameSnapshotTest {

    private List<Player> players;
    private List<Card> communityCards;
    private int pot;

    @BeforeEach
    public void setUp() {
        players = new ArrayList<>();
        players.add(new Player("Alice", true, 1000));
        players.add(new Player("Bot1", false, 1000));
        
        communityCards = new ArrayList<>();
        communityCards.add(new Card(Card.Suit.HEARTS, 10));
        communityCards.add(new Card(Card.Suit.SPADES, 14)); // Ace
        
        pot = 500;
    }

    @Test
    public void testSnapshotCopiesDataCorrectly() {
        GameSnapshot snapshot = new GameSnapshot(players, communityCards, pot);

        assertEquals(2, snapshot.playerNames.size());
        assertEquals("Alice", snapshot.playerNames.get(0));
        assertEquals("Bot1", snapshot.playerNames.get(1));
        
        assertEquals(2, snapshot.chipCounts.size());
        assertEquals(1000, snapshot.chipCounts.get(0));
        
        assertEquals(2, snapshot.communityCards.size());
        assertEquals(Card.Suit.HEARTS, snapshot.communityCards.get(0).getSuit());
        
        assertEquals(500, snapshot.pot);
    }

    @Test
    public void testOriginalListModificationDoesNotAffectSnapshot() {
        GameSnapshot snapshot = new GameSnapshot(players, communityCards, pot);
        
        // Modify the original lists passed into the snapshot
        players.add(new Player("LateJoiner", false, 500));
        communityCards.add(new Card(Card.Suit.CLUBS, 2));
        
        // The snapshot should remain unchanged
        assertEquals(2, snapshot.playerNames.size(), "Snapshot player list should not change when original list changes");
        assertEquals(2, snapshot.communityCards.size(), "Snapshot community cards should not change when original list changes");
    }

    @Test
    public void testSnapshotListsAreImmutable() {
        GameSnapshot snapshot = new GameSnapshot(players, communityCards, pot);

        // Attempting to modify the unmodifiable lists should throw UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () -> {
            snapshot.playerNames.add("Hacker");
        }, "Should throw exception when trying to modify playerNames");

        assertThrows(UnsupportedOperationException.class, () -> {
            snapshot.chipCounts.add(9999);
        }, "Should throw exception when trying to modify chipCounts");

        assertThrows(UnsupportedOperationException.class, () -> {
            snapshot.communityCards.add(new Card(Card.Suit.DIAMONDS, 5));
        }, "Should throw exception when trying to modify communityCards");
    }
}