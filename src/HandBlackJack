package BlackJack;

import java.util.ArrayList;
/**
 * A hand of cards for a player or the dealer.
 */
public class Hand
{
    private ArrayList<Card> playersCards;
    /**
     * Creates an empty hand.
     */
    public Hand() {
        playersCards = new ArrayList<Card>();
    }
    /**
     * Adds a card to the hand.
     *
     * @param card the card to add
     */
    public void addCard(Card card) {
        playersCards.add(card);
    }
    /**
     * Gets the card at a given position in the hand.
     *
     * @param index the position of the card (0 is the first card dealt)
     * @return the card at that position
     */
    public Card getCard(int index)
    {
        return playersCards.get(index);
    }
    /**
     * Calculates the best total for the hand. Aces count as 11
     * unless that would bust the hand, in which case they count as 1.
     *
     * @return the hand's total
     */
    public int getTotal() {
        int total = 0;
        int aces = 0;
        
        
        for (Card card: playersCards) {
            total += card.getValue();
            if (card.getRank().equals("Ace"))
            {
                aces++;
            } 
        }
        
        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }
    /**
     * @return true if the hand is over 21
     */
    public boolean Bust() {
        return getTotal() > 21;
    }
    /**
     * @return true if the hand is a two-card 21
     */
    public boolean Blackjack() {
        return getTotal() == 21;
    }

    /**
     * @return the number of cards in the hand
     */
    public int size() {
        return playersCards.size();
    }
    /**
     * Empties the hand for a new round.
     */
    public void clear(){
        playersCards.clear();
    }
    @Override
    public String toString() {
        return playersCards.toString() + " (total: " + getTotal() + ")";
    }
        

}
