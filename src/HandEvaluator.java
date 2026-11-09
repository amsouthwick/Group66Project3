import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates the best 5 cards
 */
public class HandEvaluator
{
    /**
     * Orders players from strongest to weakest. Equal hands stay in table order
     * @param players players to compare (normally players who have not folded)
     * @param board the five community cards
     * @return a new list with the best hand first; does not change the input list
     */
    public List<Player> rankHands(List<Player> players, List<Card> board)
    {
        if (players == null || players.isEmpty())
        {
            throw new IllegalArgumentException("At least one player is needed.");
        }
        ArrayList<Player> ranked = new ArrayList<Player>(players);
        int[] values = new int[ranked.size()];
        for (int i = 0; i < ranked.size(); i++)
        {
            values[i] = getHandValue(ranked.get(i), board);
        }

        for (int end = ranked.size() - 1; end > 0; end--)
        {
            for (int i = 0; i < end; i++)
            {
                if (values[i] < values[i + 1])
                {
                    int oldValue = values[i];
                    values[i] = values[i + 1];
                    values[i + 1] = oldValue;
                    Player oldPlayer = ranked.get(i);
                    ranked.set(i, ranked.get(i + 1));
                    ranked.set(i + 1, oldPlayer);
                }
            }
        }
        return ranked;
    }

    /**
     * Returns just the best player or all players tied for the best hand
     * @param players players eligible to win this pot
     * @param board the five community cards
     * @return the winning players
     */
    public List<Player> getWinners(List<Player> players, List<Card> board)
    {
        List<Player> ranked = rankHands(players, board);
        ArrayList<Player> winners = new ArrayList<Player>();
        int best = getHandValue(ranked.get(0), board);
        for (Player player : ranked)
        {
            if (getHandValue(player, board) == best)
            {
                winners.add(player);
            }
        }
        return winners;
    }

    /**
     * Gives a hand an integer score
     * @param player the player whose two cards should be used
     * @param board exactly five community cards
     * @return the score of the best five-card combination
     */
    public int getHandValue(Player player, List<Card> board)
    {
        if (board == null || board.size() != 5)
        {
            throw new IllegalArgumentException("The board must have five cards.");
        }
        if (player == null || player.getCards().size() != 2)
        {
            throw new IllegalStateException("The player must have two cards.");
        }
        ArrayList<Card> cards = new ArrayList<Card>(player.getCards());
        cards.addAll(board);
        for (int i = 0; i < cards.size(); i++)
        {
            Card card = cards.get(i);
            if (card == null || card.getSuit() == null)
            {
                throw new IllegalArgumentException("Cards must have a suit and rank.");
            }
            for (int j = 0; j < i; j++)
            {
                if (card.getRank() == cards.get(j).getRank()
                    && card.getSuit() == cards.get(j).getSuit())
                {
                    throw new IllegalArgumentException("A card was used twice.");
                }
            }
        }

        int best = 0;
    
        for (int first = 0; first < 6; first++)
        {
            for (int second = first + 1; second < 7; second++)
            {
                Card[] five = new Card[5];
                int position = 0;
                for (int i = 0; i < cards.size(); i++)
                {
                    if (i != first && i != second)
                    {
                        five[position] = cards.get(i);
                        position++;
                    }
                }
                int value = scoreFiveCards(five);
                if (value > best)
                {
                    best = value;
                }
            }
        }
        return best;
    }

    public int scoreFiveCards(Card[] cards)
    {
        int[] counts = new int[15];
        boolean flush = true;
        for (Card card : cards)
        {
            counts[card.getRank()]++;
            if (card.getSuit() != cards[0].getSuit())
            {
                flush = false;
            }
        }

        int straightHigh = 0;
        for (int high = 14; high >= 6; high--)
        {
            boolean straight = true;
            for (int rank = high; rank > high - 5; rank--)
            {
                if (counts[rank] == 0)
                {
                    straight = false;
                }
            }
            if (straight)
            {
                straightHigh = high;
                break;
            }
        }
        if (straightHigh == 0 && counts[14] > 0 && counts[2] > 0
            && counts[3] > 0 && counts[4] > 0 && counts[5] > 0)
        {
            straightHigh = 5;
        }

        int four = 0;
        int three = 0;
        int highPair = 0;
        int lowPair = 0;
        int[] singles = new int[5];
        int singleCount = 0;
        for (int rank = 14; rank >= 2; rank--)
        {
            if (counts[rank] == 4)
            {
                four = rank;
            }
            else if (counts[rank] == 3)
            {
                three = rank;
            }
            else if (counts[rank] == 2)
            {
                if (highPair == 0)
                {
                    highPair = rank;
                }
                else
                {
                    lowPair = rank;
                }
            }
            else if (counts[rank] == 1)
            {
                singles[singleCount] = rank;
                singleCount++;
            }
        }

        if (flush && straightHigh > 0)
        {
            return makeScore(8, new int[] {straightHigh, 0, 0, 0, 0});
        }
        if (four > 0)
        {
            return makeScore(7, new int[] {four, singles[0], 0, 0, 0});
        }
        if (three > 0 && highPair > 0)
        {
            return makeScore(6, new int[] {three, highPair, 0, 0, 0});
        }
        if (flush)
        {
            return makeScore(5, singles);
        }
        if (straightHigh > 0)
        {
            return makeScore(4, new int[] {straightHigh, 0, 0, 0, 0});
        }
        if (three > 0)
        {
            return makeScore(3, new int[] {three, singles[0], singles[1], 0, 0});
        }
        if (lowPair > 0)
        {
            return makeScore(2, new int[] {highPair, lowPair, singles[0], 0, 0});
        }
        if (highPair > 0)
        {
            return makeScore(1, new int[] {
                highPair, singles[0], singles[1], singles[2], 0});
        }
        return makeScore(0, singles);
    }

    private int makeScore(int category, int[] ranks)
    {
        int score = category;
        for (int rank : ranks)
        {
            score = score * 15 + rank;
        }
        return score;
    }
}

