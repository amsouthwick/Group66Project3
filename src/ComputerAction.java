package casinoApp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Chooses a computer's move using only its own cards and public info
 */
public class ComputerAction
{
    private static int SIMULATIONS = 500;
    private static double VALUE_BET_RATE = 0.75;
    private static double DRAW_BET_RATE = 0.30;
    private static double WEAK_BET_RATE = 0.08;

    private final Random random;
    private final HandEvaluator evaluator;
    private List<Card> hand;
    private List<Card> board;
    private ArrayList<Card> unseen;
    private int betAmount;
    private int drawOuts;
    private boolean nuts;
    private boolean bluffing;
    private double equity;
    private double potOdds;
    private double expectedCallProfit;
    private double blockerScore;
    private double[] bluffChances;

    /** Creates a computer strategy with normal randomness. */
    public ComputerAction()
    {
        this(new Random());
    }

    /**
     * Allows repeatable decisions in tests, using new Random(seed)
     * @param random the source of random numbers
     */
    public ComputerAction(Random random)
    {
        if (random == null)
        {
            throw new IllegalArgumentException("Random cannot be null.");
        }
        this.random = random;
        evaluator = new HandEvaluator();
    }

  
    public String chooseAction(Player computer, List<Card> communityCards,
        int availablePot, int amountToCall, int[] opponentAggression,
        boolean everyoneCanFold, List<String> legalActions)
    {
        if (opponentAggression == null)
        {
            throw new IllegalArgumentException("Opponent information is required.");
        }
        int[] noFurtherCalls = new int[opponentAggression.length];
        return chooseAction(computer, communityCards, availablePot, amountToCall,
            opponentAggression, noFurtherCalls, noFurtherCalls,
            everyoneCanFold, legalActions);
    }

  
    public String chooseAction(Player computer, List<Card> communityCards,
        int availablePot, int amountToCall, int[] opponentAggression,
        int[] opponentCallCosts, int[] opponentPotAdds,
        boolean everyoneCanFold, List<String> legalActions)
    {
        if (computer == null || computer.getCards().size() != 2
            || computer.getChips() <= 0 || communityCards == null
            || (communityCards.size() != 0 && communityCards.size() != 3
                && communityCards.size() != 4 && communityCards.size() != 5)
            || availablePot < 0 || amountToCall < 0
            || opponentAggression == null || opponentAggression.length < 1
            || opponentAggression.length > 7
            || opponentCallCosts == null || opponentPotAdds == null
            || opponentCallCosts.length != opponentAggression.length
            || opponentPotAdds.length != opponentAggression.length
            || legalActions == null || legalActions.isEmpty())
        {
            throw new IllegalArgumentException("Invalid computer turn.");
        }
        for (int i = 0; i < opponentAggression.length; i++)
        {
            if (opponentAggression[i] < 0 || opponentCallCosts[i] < 0
                || opponentPotAdds[i] < 0
                || opponentPotAdds[i] > opponentCallCosts[i])
            {
                throw new IllegalArgumentException("Invalid opponent information.");
            }
        }
        hand = computer.getCards();
        board = new ArrayList<Card>(communityCards);
        unseen = makeDeck(false);
        // This also catches duplicate or invalid visible cards.
        if (unseen.size() != 52 - hand.size() - board.size())
        {
            throw new IllegalArgumentException("Cards must be distinct.");
        }
        betAmount = 0;
        bluffing = false;
        analyzeNutsAndBlockers();
        drawOuts = countDrawOuts();
        int chips = computer.getChips();
        int callCost = Math.min(chips, amountToCall);
        estimateEquity(opponentAggression, opponentCallCosts, opponentPotAdds,
            availablePot, callCost);
        potOdds = 0.0;
        if (callCost > 0)
        {
            potOdds = callCost / ((double)availablePot + callCost);
        }
        int opponents = opponentAggression.length;
        double fairShare = 1.0 / (opponents + 1);
        double valueThreshold = Math.max(0.45, fairShare + 0.18);
        if (nuts)
        {
            valueThreshold = Math.max(0.45, fairShare + 0.12);
        }

        if (legalActions.contains("bet") || legalActions.contains("raise"))
        {
            double extra = (availablePot + (double)callCost)
                * (0.50 + random.nextDouble() * 0.25);
            extra = Math.max(extra, Math.max(1, chips / 20));
            int amount = (int)Math.min(chips, amountToCall + extra);

            boolean valueBet = equity >= valueThreshold
                && equity >= potOdds + 0.10;
            boolean bluff = false;
            if (!valueBet && !nuts && everyoneCanFold && availablePot > 0
                && opponents <= 2 && (equity < fairShare
                    || (drawOuts > 0 && board.size() < 5)))
            {
                bluff = shouldBluff(availablePot, amount, amountToCall,
                    opponentAggression);
            }
            if (valueBet || bluff)
            {
                betAmount = amount;
                bluffing = bluff;
                if (legalActions.contains("bet"))
                {
                    return "bet";
                }
                return "raise";
            }
        }

        if (legalActions.contains("check"))
        {
            return "check";
        }
        // A pending caller affects both our opponents and the pot we can win.
        // Comparing multiway equity to the current pot alone is too pessimistic.
        if (legalActions.contains("call") && expectedCallProfit >= -0.000000001)
        {
            return "call";
        }
        return "fold";
    }

    private boolean shouldBluff(int pot, int amount, int toCall,
        int[] aggression)
    {
        int pressure = 0;
        for (int count : aggression)
        {
            pressure += count;
        }
        if (pressure >= 2)
        {
            return false; // Keep beginner bots from repeatedly bluff-raising.
        }
        double chance = 0.04 + Math.max(0, blockerScore) * 0.60;
        boolean semiBluff = drawOuts > 0 && board.size() < 5;
        if (semiBluff)
        {
            chance += Math.min(0.18, getDrawChance() * 0.70);
        }
        if (toCall > 0)
        {
            chance *= 0.5;
        }
        chance /= aggression.length;

        // A rough fold estimate, not a known fact about the opponents.
        double oneFold = Math.max(0.10,
            Math.min(0.65, 0.40 + blockerScore * 0.60 - pressure * 0.10));
        double allFold = 1.0;
        for (int i = 0; i < aggression.length; i++)
        {
            allFold *= oneFold;
        }
        double pureBluffProfit = allFold * pot - (1 - allFold) * amount;
        if (!semiBluff && pureBluffProfit <= 0)
        {
            return false;
        }
        return random.nextDouble() < Math.min(0.25, chance);
    }

    /** Makes a hypothetical deck. It never accesses GameHandler's real deck. */
    private ArrayList<Card> makeDeck(boolean includeOwnCards)
    {
        ArrayList<Card> cards = new ArrayList<Card>();
        for (Card.Suit suit : Card.Suit.values())
        {
            for (int rank = 2; rank <= 14; rank++)
            {
                Card card = new Card(suit, rank);
                if (!containsCard(board, card)
                    && (includeOwnCards || !containsCard(hand, card)))
                {
                    cards.add(card);
                }
            }
        }
        return cards;
    }

    private boolean containsCard(List<Card> cards, Card target)
    {
        for (Card card : cards)
        {
            if (card == null || card.getSuit() == null)
            {
                throw new IllegalArgumentException("Cards need a suit and rank.");
            }
            if (card.getRank() == target.getRank()
                && card.getSuit() == target.getSuit())
            {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Card> pair(Card first, Card second)
    {
        ArrayList<Card> result = new ArrayList<Card>();
        result.add(first);
        result.add(second);
        return result;
    }

    private void analyzeNutsAndBlockers()
    {
        nuts = board.size() >= 3;
        int ownScore = 0;
        if (nuts)
        {
            ownScore = evaluator.getHandValue(hand, board);
        }
        // Include our cards to measure which hypothetical hands they remove.
        ArrayList<Card> candidates = makeDeck(true);
        double valueTotal = 0;
        double valueBlocked = 0;
        double bluffTotal = 0;
        double bluffBlocked = 0;
        for (int i = 0; i < candidates.size(); i++)
        {
            for (int j = i + 1; j < candidates.size(); j++)
            {
                List<Card> possible = pair(candidates.get(i), candidates.get(j));
                boolean blocked = containsCard(hand, possible.get(0))
                    || containsCard(hand, possible.get(1));
                if (nuts && !blocked
                    && evaluator.getHandValue(possible, board) > ownScore)
                {
                    nuts = false;
                }
                int type = handType(possible);
                if (type == 2)
                {
                    valueTotal++;
                    if (blocked)
                    {
                        valueBlocked++;
                    }
                }
                else
                {
                    double weight = bettingRate(type);
                    bluffTotal += weight;
                    if (blocked)
                    {
                        bluffBlocked += weight;
                    }
                }
            }
        }
        blockerScore = 0;
        if (valueTotal > 0 && bluffTotal > 0)
        {
            // Blocking value helps a bluff; blocking bluffs works against it.
            blockerScore = valueBlocked / valueTotal - bluffBlocked / bluffTotal;
        }
    }

    private int countDrawOuts()
    {
        if (board.size() != 3 && board.size() != 4)
        {
            return 0;
        }
        int current = evaluator.getHandValue(hand, board);
        int outs = 0;
        for (Card card : unseen)
        {
            ArrayList<Card> nextBoard = new ArrayList<Card>(board);
            nextBoard.add(card);
            int next = evaluator.getHandValue(hand, nextBoard);
            // Count each card once, even if it makes both straight and flush.
            if (next > current && evaluator.getCategory(next) >= 4
                && (nextBoard.size() < 5 || next > boardScore(nextBoard)))
            {
                outs++;
            }
        }
        return outs;
    }

    private void estimateEquity(int[] aggression, int[] callCosts,
        int[] potAdds, int availablePot, int ownCallCost)
    {
        bluffChances = new double[aggression.length];
        double totalWeight = 0;
        double earnedShare = 0;
        double earnedChips = 0;
        for (int trial = 0; trial < SIMULATIONS; trial++)
        {
            ArrayList<Card> deck = new ArrayList<Card>(unseen);
            ArrayList<List<Card>> opponents = new ArrayList<List<Card>>();
            boolean[] possibleBluff = new boolean[aggression.length];
            boolean[] staysInHand = new boolean[aggression.length];
            double weight = 1.0;
            double possiblePot = (double)availablePot + ownCallCost;
            for (int i = 0; i < aggression.length; i++)
            {
                List<Card> cards = pair(drawCard(deck), drawCard(deck));
                opponents.add(cards);
                int type = handType(cards);
                possibleBluff[i] = type != 2;
                // Weight using the CURRENT board, not the simulated future.
                // Repeat raises make strong holdings more likely in this model.
                for (int bet = 0; bet < Math.min(3, aggression[i]); bet++)
                {
                    weight *= bettingRate(type);
                }
                // Matched bets and all-in players are already in the pot.
                staysInHand[i] = true;
                if (callCosts[i] > 0)
                {
                    double chance = continuationChance(type, callCosts[i],
                        (double)availablePot + ownCallCost);
                    staysInHand[i] = random.nextDouble() < chance;
                    if (staysInHand[i])
                    {
                        possiblePot += potAdds[i];
                    }
                }
            }
            ArrayList<Card> finalBoard = new ArrayList<Card>(board);
            while (finalBoard.size() < 5)
            {
                finalBoard.add(drawCard(deck));
            }
            int own = evaluator.getHandValue(hand, finalBoard);
            int tied = 1;
            boolean beaten = false;
            for (int i = 0; i < opponents.size(); i++)
            {
                if (staysInHand[i])
                {
                    int other = evaluator.getHandValue(opponents.get(i), finalBoard);
                    if (other > own)
                    {
                        beaten = true;
                    }
                    else if (other == own)
                    {
                        tied++;
                    }
                }
            }
            totalWeight += weight;
            if (!beaten)
            {
                earnedShare += weight / tied;
                earnedChips += weight * possiblePot / tied;
            }
            for (int i = 0; i < aggression.length; i++)
            {
                if (aggression[i] > 0 && possibleBluff[i])
                {
                    bluffChances[i] += weight;
                }
            }
        }
        equity = earnedShare / totalWeight;
        expectedCallProfit = earnedChips / totalWeight - ownCallCost;
        for (int i = 0; i < aggression.length; i++)
        {
            if (aggression[i] == 0)
            {
                bluffChances[i] = -1; // No bet/raise to interpret this round.
            }
            else
            {
                bluffChances[i] /= totalWeight;
            }
        }
    }

    /**
     * A simple assumed response for an opponent who still needs to pay.
     * Stronger hands continue more often; expensive draws continue less often.
     * This does not choose that opponent's real move or see its actual cards.
     */
    private double continuationChance(int type, int cost, double currentPot)
    {
        double price = cost / (currentPot + cost);
        if (type == 2)
        {
            return Math.max(0.50, 0.95 - price * 0.20);
        }
        if (type == 1)
        {
            return Math.max(0.10, 0.80 - price);
        }
        return Math.max(0.02, 0.25 - price * 0.40);
    }

    private Card drawCard(ArrayList<Card> deck)
    {
        return deck.remove(random.nextInt(deck.size()));
    }

    /** 2 = plausible value hand, 1 = draw, 0 = weak hand. */
    private int handType(List<Card> cards)
    {
        if (board.isEmpty())
        {
            int first = cards.get(0).getRank();
            int second = cards.get(1).getRank();
            if ((first == second && first >= 10)
                || (Math.min(first, second) >= 11))
            {
                return 2;
            }
            if (first == second || (cards.get(0).getSuit() == cards.get(1).getSuit()
                && Math.abs(first - second) <= 2))
            {
                return 1; // Speculative preflop hand, not a literal draw yet.
            }
            return 0;
        }
        int score = evaluator.getHandValue(cards, board);
        int category = evaluator.getCategory(score);
        if (board.size() == 5 && score == boardScore(board))
        {
            return 0; // Playing the board does not give a private advantage.
        }
        if (category >= 4)
        {
            return 2;
        }
        int[] boardCounts = new int[15];
        int[] counts = new int[15];
        int topBoard = 0;
        for (Card card : board)
        {
            boardCounts[card.getRank()]++;
            counts[card.getRank()]++;
            topBoard = Math.max(topBoard, card.getRank());
        }
        for (Card card : cards)
        {
            counts[card.getRank()]++;
        }
        int pairsSeen = 0;
        for (int rank = 14; rank >= 2; rank--)
        {
            if (category == 3 && counts[rank] == 3 && boardCounts[rank] < 3)
            {
                return 2;
            }
            if (counts[rank] == 2)
            {
                pairsSeen++;
                if (boardCounts[rank] < 2
                    && ((category == 2 && pairsSeen <= 2)
                        || (category == 1 && rank >= topBoard)))
                {
                    return 2;
                }
            }
        }
        if (board.size() < 5 && hasDraw(cards))
        {
            return 1;
        }
        return 0;
    }

    private boolean hasDraw(List<Card> cards)
    {
        ArrayList<Card> all = new ArrayList<Card>(board);
        all.addAll(cards);
        for (Card.Suit suit : Card.Suit.values())
        {
            int count = 0;
            for (Card card : all)
            {
                if (card.getSuit() == suit)
                {
                    count++;
                }
            }
            if (count == 4 && (cards.get(0).getSuit() == suit
                || cards.get(1).getSuit() == suit))
            {
                return true;
            }
        }
        boolean[] ranks = new boolean[15];
        boolean[] boardRanks = new boolean[15];
        for (Card card : all)
        {
            ranks[card.getRank()] = true;
        }
        for (Card card : board)
        {
            boardRanks[card.getRank()] = true;
        }
        ranks[1] = ranks[14]; // Ace can be low in A-2-3-4-5.
        boardRanks[1] = boardRanks[14];
        for (int low = 1; low <= 10; low++)
        {
            int count = 0;
            boolean usesHand = false;
            for (int rank = low; rank < low + 5; rank++)
            {
                if (ranks[rank])
                {
                    count++;
                    if (!boardRanks[rank])
                    {
                        usesHand = true;
                    }
                }
            }
            if (count == 4 && usesHand)
            {
                return true;
            }
        }
        return false;
    }

    private double bettingRate(int type)
    {
        if (type == 2)
        {
            return VALUE_BET_RATE;
        }
        if (type == 1)
        {
            return DRAW_BET_RATE;
        }
        return WEAK_BET_RATE;
    }

    private int boardScore(List<Card> fiveCards)
    {
        Card[] cards = new Card[5];
        for (int i = 0; i < 5; i++)
        {
            cards[i] = fiveCards.get(i);
        }
        return evaluator.scoreFiveCards(cards);
    }

    /** @return new chips to add for bet/raise; zero for other actions */
    public int getBetAmount()
    {
        return betAmount;
    }

    /** @return estimated showdown share, including split pots, from 0 to 1 */
    public double getEquity()
    {
        return equity;
    }

    /** @return pot odds before any additional opponents call */
    public double getPotOdds()
    {
        return potOdds;
    }

    /** @return estimated net chips from calling, including possible callers */
    public double getExpectedCallProfit()
    {
        return expectedCallProfit;
    }

    /** @return true if no possible opponent hand currently beats this hand */
    public boolean hasNuts()
    {
        return nuts;
    }

    /**
     * @return cards improving to straight-or-better using a hole card;
     * these are potential outs, not guaranteed winning outs
     */
    public int getDrawOuts()
    {
        return drawOuts;
    }

    /** @return chance of one of those outs on the next card, not by the river */
    public double getDrawChance()
    {
        return drawOuts / (double)unseen.size();
    }

    /** @return value-removal fraction minus bluff-removal fraction */
    public double getBlockerScore()
    {
        return blockerScore;
    }

    /**
     * @param opponent index matching the opponentAggression array
     * @return modeled bluff/semi-bluff probability, or -1 if no bet was observed
     */
    public double getBluffChance(int opponent)
    {
        return bluffChances[opponent];
    }

    /** @return whether the selected move was a bluff or semi-bluff */
    public boolean isBluffing()
    {
        return bluffing;
    }
}


