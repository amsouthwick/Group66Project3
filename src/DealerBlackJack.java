package BlackJack;
/**
 * The dealer. Follows a fixed rule: hit until reaching 17 or more.
 */
public class Dealer
    extends Player
{
    private static final int HIT_LIMIT = 17;
    /**
     * Creates the dealer.
     * @param name dealer's name
     * @param chips dealer's chips
     */
    public Dealer(String name, int chips)
    {
        super(name, chips);
        // TODO Auto-generated constructor stub
    }
    /**
     * @return true if the dealer must take another card
     */
    public boolean dealerHit() {
        return getHand().getTotal() < HIT_LIMIT;
    }
    /**
     * Plays out the dealer's turn by drawing from the deck
     * until the dealer has 17 or more (or busts).
     *
     * @param deck the deck to draw from
     */
    public void playTurn(Deck deck) {
        while (dealerHit()) {
            recieveCard(deck.deals());
        }
    }

}
