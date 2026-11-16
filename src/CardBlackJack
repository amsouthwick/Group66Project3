package BlackJack;
// -------------------------------------------------------------------------
/**
 *  
 *  Creates the cards including their suit and rank
 */
public class Card
{

    // -------------------------------------------------------------------------
    /**
     * the different suits in a deck of cards
     * 
     */
    public enum Suit
    {

        /**
         * ♥️
         */
        HEARTS,
        /**
         * ♦️
         */
        DIAMONDS,
        /**
         * ♣️
         */
        CLUBS,
        /**
         * ♠️
         */
        SPADES
    }

    private final Suit suit;
    private final String rank;

    // ----------------------------------------------------------
    /**
     * Create a new Card object.
     * 
     * @param suit
     * @param rank
     */
    public Card(Suit suit, String rank)
    {
        this.suit = suit;
        this.rank = rank;
    }
    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     * 
     * @return suit
     */
    public Suit getSuit()
    {
        return suit;
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     * 
     * @return rank
     */
    public String getRank()
    {
        return rank;
    }
    
    // ----------------------------------------------------------
    /**
     * Gets value of card
     * @return 11 if card is an Ace, 10 if card is a face, the card value
     * 
     */
    public int getValue()
    {
        if (rank.equals("Ace")) {
            return 11;
        }
        else if (rank.equals("King")||rank.equals("Jack")||rank.equals("Queen")) 
        {
            return 10;
        }
        else {
            return Integer.parseInt(rank);
        }
    }
    
    @Override
    public String toString()
    {
        return rank + " of " + suit;
    }

}
