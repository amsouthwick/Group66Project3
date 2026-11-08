import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An immutable, read-only summary of the current game state
 * that the Model passes to the View (PokerApp).
 */
public class GameSnapshot {

    /** The names of the players currently at the table. */
    public final List<String> playerNames;

    /** The chip counts corresponding to each player. */
    public final List<Integer> chipCounts;

    /** The community cards currently visible on the board. */
    public final List<Card> communityCards;

    /** The current total amount of chips in the pot. */
    public final int pot;

    /**
     * Constructs a new snapshot of the game state.
     * Extracts only the information the view needs to render.
     *
     * @param players a list of the current players in the game
     * @param communityCards the list of community cards currently dealt
     * @param pot the current size of pot
     */
    public GameSnapshot(List<Player> players, List<Card> communityCards, int pot) {
        List<String> names = new ArrayList<>();
        List<Integer> chips = new ArrayList<>();

        for (Player p : players) {
            names.add(p.getName());
            chips.add(p.getChips());
        }

        this.playerNames = Collections.unmodifiableList(names);
        this.chipCounts = Collections.unmodifiableList(chips);
        this.communityCards = Collections.unmodifiableList(new ArrayList<>(communityCards));
        this.pot = pot;
    }
}