
// -------------------------------------------------------------------------
/**
 * Creates a card class that has a suit and rank 7
 * 
 * @author micha
 * @version Nov 9, 2026
 */
public class Card
{

    // -------------------------------------------------------------------------
    /**
     * the different suits in a deck of cards
     * 
     * @author michaela
     * @version Nov 9, 2026
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
    private final int rank;

    // ----------------------------------------------------------
    /**
     * Create a new Card object.
     * 
     * @param suit
     * @param rank
     */
    public Card(Suit suit, int rank)
    {
        if (rank < 2 || rank > 14)
        {
            throw new IllegalArgumentException(
                "Rank must be between 2 and 14.");
        }
        this.suit = suit;
        this.rank = rank;
    }

    // ~Public Methods ........................................................


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     * 
     * @return suit
     */

    @Override
    public String toString()
    {
        String rankName;

        switch (rank)
        {
            case 11:
                rankName = "Jack";
                break;
            case 12:
                rankName = "Queen";
                break;
            case 13:
                rankName = "King";
                break;
            case 14:
                rankName = "Ace";
                break;
            default:
                rankName = String.valueOf(rank);
        }

        return rankName + " of " + suit;
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
    public int getRank()
    {
        return rank;
    }

}
