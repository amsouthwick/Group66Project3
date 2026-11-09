/**
 * Represents the available actions a player can take during their turn.
 * Enforces the rules for betting, checking, raising, and folding based on game state.
 */
public class PlayerAction {
    private final Player player;
    private final int betToMatch;
    private final boolean isFirst;

    /**
     * Builds one turn's options.
     * @param p the player whose turn it is
     * @param betToMatch the current bet amount to match
     * @param isFirst true if this player is the first to act
     * @throws IllegalArgumentException if the player is null
     */
    public PlayerAction(Player p, int betToMatch, boolean isFirst) {
        if (p == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        this.player = p;
        this.betToMatch = betToMatch;
        this.isFirst = isFirst;
    }

    /**
     * Passes the turn without betting.
     * @return 0, representing no chips committed
     * @throws IllegalStateException if facing a bet
     */
    public int check() {
        if (betToMatch > 0) {
            throw new IllegalStateException("Cannot check when facing a bet");
        }
        return 0;
    }

    /**
     * Places an opening bet.
     *
     * @param amount the amount to bet
     * @return the amount of chips committed
     * @throws IllegalStateException if not the first to act
     * @throws IllegalArgumentException if amount is not positive or over the player's chips
     */
    public int bet(int amount) {
        if (!isFirst) {
            throw new IllegalStateException("Can only place an opening bet if first to act");
        }
        if (amount <= 0 || amount > player.getChips()) {
            throw new IllegalArgumentException("Bet amount must be positive and within chip limits");
        }
        return amount;
    }

    /**
     * Matches the bet and raises.
     * @param amount the total amount of chips committed for this action
     * @return the chips committed
     * @throws IllegalStateException if there is no bet to raise
     * @throws IllegalArgumentException if the amount does not exceed the bet to match or is over chips
     */
    public int raise(int amount) {
        if (betToMatch <= 0) {
            throw new IllegalStateException("No bet to raise");
        }
        if (amount <= betToMatch || amount > player.getChips()) {
            throw new IllegalArgumentException(
                "Raise must exceed the bet to match and be within chip limits");
        }
        return amount;
    }

    /**
     * Marks the player as folded.
     * @throws IllegalStateException if the player is already folded
     */
    public void fold() {
        if (player.isFolded()) {
            throw new IllegalStateException("Player is already folded");
        }
        player.setFolded(true);
    }
}