
public class Card
{

    // -------------------------------------------------------------------------
    /**
     *  the different suits in a deck of cards
     * 
     *  @author michaela
     *  @version Nov 9, 2026
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
     

    public Card(Suit suit, int rank)
    {
        
        this.suit = suit;
        this.rank = rank;
    }


    // ~Public Methods ........................................................
    
    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     * @return suit
     */
    


    @Override
    public String toString()
    {
        return rank + " of " + suit;
    }


    public Suit getSuit()
    {
        return suit;
    }


    public int getRank()
    {
        return rank;
    }

}
