public class Card
{
    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }

    public enum Rank {
        TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN,
        JACK, QUEEN, KING, ACE
    }
    
    //~ Fields ................................................................
    Private final Suit suit;
    Private final Rank value;
    //~ Constructors ..........................................................
    
    
    //~Public  Methods ........................................................
    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return value;
    }
    
    @Override
    public String toString() {
        return value + " of " + suit;
    }
    
}
