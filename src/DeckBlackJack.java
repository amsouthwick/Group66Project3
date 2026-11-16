package BlackJack;

import BlackJack.Card.Suit;
import java.util.ArrayList;
import java.util.Collections;

// -------------------------------------------------------------------------
/**
 *  A deck of cards and player and Dealer use
 *  
 */
public class Deck
{
    private LinkedQueue<Card> cards;
    
    private static final String[] SUITS = {"Hearts", "Diamonds", "Clubs", "Spades"};
    private static final String[] RANKS = {
        "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "Jack", "Queen", "King", "Ace"};
    
    // ----------------------------------------------------------
    /**
     * Create a new Deck object.
     */
    public Deck() {
        cards = new LinkedQueue<Card>();
        build();
    }
    
    private void build() {
        ArrayList<Card> temp = new ArrayList<Card>();
        
        for(Card.Suit suit: Card.Suit.values()) {
            for (String rank: RANKS) {
                temp.add(new Card(suit, rank));
            }
        }
        Collections.shuffle(temp);
        for (Card card : temp) {
            cards.enqueue(card);
        }
    }
    
    // ----------------------------------------------------------
    /**
     * Deals the cards
     * @return a deck of cards
     */
    public Card deals() {
        if (cards.isEmpty()) {
            build();
        }
        return cards.dequeue();
    }
    /**
     * @return the number of remaining cards
     */
    public int remainingCard() {
        return cards.size();
    }
    
}
