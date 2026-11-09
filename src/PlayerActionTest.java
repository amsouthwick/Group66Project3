import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PlayerAction verifying turn rules and chip limits.
 */
public class PlayerActionTest {
    
    private Player player;

    @BeforeEach
    public void setUp() {
        player = new Player("Aidan", true, 1000);
    }
    @Test
    public void testConstructorNullPlayer() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new PlayerAction(null, 0, true);
        });
        assertEquals("Player cannot be null", exception.getMessage());
    }
    @Test
    public void testFirstToActCheck() {
        PlayerAction action = new PlayerAction(player, 0, true);
        assertEquals(0, action.check(), "First to act should be able to check (0 chips)");
    }

    @Test
    public void testFirstToActBet() {
        PlayerAction action = new PlayerAction(player, 0, true);
        assertEquals(50, action.bet(50), "Bet should return the amount committed");
    }

    @Test
    public void testRaiseOverBet() {
        PlayerAction action = new PlayerAction(player, 50, false);
        assertEquals(100, action.raise(100), "Raise should return total chips committed");
    }
    @Test
    public void testCheckFacingBet() {
        PlayerAction action = new PlayerAction(player, 50, false);
        assertThrows(IllegalStateException.class, () -> {
            action.check();
        }, "Should throw IllegalStateException when trying to check facing a bet");
    }
    @Test
    public void testBetNegativeAmount() {
        PlayerAction action = new PlayerAction(player, 0, true);
        assertThrows(IllegalArgumentException.class, () -> {
            action.bet(-10);
        }, "Should throw IllegalArgumentException for a negative bet");
    }
    @Test
    public void testRaiseWithNoBet() {
        PlayerAction action = new PlayerAction(player, 0, true);
        assertThrows(IllegalStateException.class, () -> {
            action.raise(100);
        }, "Should throw IllegalStateException when trying to raise with no active bet");
    }
    @Test
    public void testDoubleFold() {
        PlayerAction action = new PlayerAction(player, 50, false);        
        action.fold();
        assertTrue(player.isFolded(), "Player should be marked as folded");
        assertThrows(IllegalStateException.class, () -> {
            action.fold();
        }, "Should throw IllegalStateException if already folded");
    }
    @Test
    public void testBetNotFirstToAct() {
        PlayerAction action = new PlayerAction(player, 0, false);
        assertThrows(IllegalStateException.class, () -> action.bet(50));
    }
    @Test
    public void testBetZeroAmount() {
        PlayerAction action = new PlayerAction(player, 0, true);
        assertThrows(IllegalArgumentException.class, () -> action.bet(0));
    }
    @Test
    public void testBetOverChips() {
        PlayerAction action = new PlayerAction(player, 0, true);
        assertThrows(IllegalArgumentException.class, () -> action.bet(1001));
    }
    @Test
    public void testBetAllChips() {
        PlayerAction action = new PlayerAction(player, 0, true);
        assertEquals(1000, action.bet(1000), "Betting exactly all chips should be allowed");
    }
    @Test
    public void testRaiseEqualToBet() {
        PlayerAction action = new PlayerAction(player, 50, false);
        assertThrows(IllegalArgumentException.class, () -> action.raise(50));
    }
    @Test
    public void testRaiseBelowBet() {
        PlayerAction action = new PlayerAction(player, 50, false);
        assertThrows(IllegalArgumentException.class, () -> action.raise(10));
    }
    @Test
    public void testRaiseOverChips() {
        PlayerAction action = new PlayerAction(player, 50, false);
        assertThrows(IllegalArgumentException.class, () -> action.raise(1001));
    }
    @Test
    public void testCheckNotFirstNoBet() {
        PlayerAction action = new PlayerAction(player, 0, false);
        assertEquals(0, action.check());
    }
}