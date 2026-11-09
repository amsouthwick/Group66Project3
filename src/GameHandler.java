import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Manages a Texas Hold'em game
 */
public class GameHandler
{
    private final ArrayList<Player> players;
    private final LinkedQueue<Player> turnQueue;
    private final LinkedQueue<Card> deckQueue;
    private final ArrayList<Card> communityCards;
    private final HandEvaluator evaluator;
    private final int startingChips;

    private final int[] roundBets;
    private final int[] handBets;
    private SessionStats stats;
    private int pot;
    private int currentBetToMatch;
    private int turnsRemaining;
    private int humanStartingChips;
    private int round; // TODO: -1 no hand, 0 preflop, 1 flop, 2 turn, 3 river
    private boolean handInProgress;

    /**
     * Creates two through eight players, each with the given starting chips.
     *
     * @param names player names, with the human first
     * @param startingChips positive number of chips per player
     */
    public GameHandler(List<String> names, int startingChips)
    {
        if (names == null || names.size() < 2 || names.size() > 8
            || startingChips <= 0)
        {
            throw new IllegalArgumentException(
                "Use 2 to 8 players and a positive starting chip count.");
        }

        players = new ArrayList<Player>();
        for (int i = 0; i < names.size(); i++)
        {
            String name = names.get(i);
            if (name == null || name.trim().isEmpty())
            {
                throw new IllegalArgumentException("Names cannot be blank.");
            }
            for (Player existing : players)
            {
                if (existing.getName().equalsIgnoreCase(name.trim()))
                {
                    throw new IllegalArgumentException("Names must be unique.");
                }
            }
            Player.PlayerType type = Player.PlayerType.BOT;
            if (i == 0)
            {
                type = Player.PlayerType.HUMAN;
            }
            players.add(new Player(name.trim(), type));
        }

        this.startingChips = startingChips;
        turnQueue = new LinkedQueue<Player>();
        deckQueue = new LinkedQueue<Card>();
        communityCards = new ArrayList<Card>();
        evaluator = new HandEvaluator();
        roundBets = new int[players.size()];
        handBets = new int[players.size()];
        resetState();
    }

    /** Rebuilds and shuffles the deck, then deals two cards to each player
    */
    public void dealHand()
    {
        if (handInProgress || !canContinue())
        {
            throw new IllegalStateException(
                "Finish the current hand and make sure two players have chips.");
        }

        ArrayList<Card> cards = new ArrayList<Card>();
        for (Card.Suit suit : Card.Suit.values())
        {
            for (int rank = 2; rank <= 14; rank++)
            {
                cards.add(new Card(suit, rank));
            }
        }
        Collections.shuffle(cards);
        while (!deckQueue.isEmpty())
        {
            deckQueue.dequeue();
        }
        for (Card card : cards)
        {
            deckQueue.enqueue(card);
        }

        communityCards.clear();
        pot = 0;
        humanStartingChips = players.get(0).getChips();
        for (int i = 0; i < players.size(); i++)
        {
            Player player = players.get(i);
            player.clearHand();
            player.setFolded(player.getChips() == 0);
            handBets[i] = 0;
        }
        for (int cardNumber = 0; cardNumber < 2; cardNumber++)
        {
            for (Player player : players)
            {
                if (!player.isFolded())
                {
                    player.addCard(deckQueue.dequeue());
                }
            }
        }
        round = 0;
        handInProgress = true;
        startBettingRound();
    }

    /** Deals three community cards after preflop betting finishes. */
    public void dealFlop()
    {
        dealCommunityCards(0, 3);
    }

    /** Deals one community card after flop betting finishes. */
    public void dealTurn()
    {
        dealCommunityCards(1, 1);
    }

    /** Deals one community card after turn betting finishes. */
    public void dealRiver()
    {
        dealCommunityCards(2, 1);
    }

    private void dealCommunityCards(int expectedRound, int count)
    {
        if (!handInProgress || round != expectedRound
            || !isBettingRoundOver() || getPlayersStillInHand().size() < 2)
        {
            throw new IllegalStateException(
                "Deal the board in order, after betting is finished.");
        }
        for (int i = 0; i < count; i++)
        {
            communityCards.add(deckQueue.dequeue());
        }
        round++;
        startBettingRound();
    }

    private void startBettingRound()
    {
        clearTurnQueue();
        currentBetToMatch = 0;
        for (int i = 0; i < players.size(); i++)
        {
            roundBets[i] = 0;
            Player player = players.get(i);
            if (!player.isFolded() && player.getChips() > 0)
            {
                turnQueue.enqueue(player);
            }
        }
        turnsRemaining = turnQueue.size();
        // No betting is needed when everyone else is already all-in.
        if (turnsRemaining < 2)
        {
            turnsRemaining = 0;
        }
    }

    /**
     * Applies the current player's action and moves to the next player.
     *
     * @param player the player whose turn it is
     * @param action check, bet, call, raise, or fold
     * @param amount new chips for bet/raise; use 0 for check/call/fold
     * @return the number of chips added to the pot
     */
    public int applyAction(Player player, String action, int amount)
    {
        requireCurrentPlayer(player);
        if (action == null || amount < 0 || amount > player.getChips())
        {
            throw new IllegalArgumentException("Invalid action or chip amount.");
        }
        action = action.trim().toLowerCase(Locale.ROOT);
        if (!getLegalActionsFor(player).contains(action))
        {
            throw new IllegalArgumentException("That action is not available.");
        }
        if (!action.equals("bet") && !action.equals("raise") && amount != 0)
        {
            throw new IllegalArgumentException("Use 0 for check, call, or fold.");
        }

        int index = players.indexOf(player);
        int needed = getAmountToCall(player);
        // TODO an opening bet is possible until somebody has placed the first bet
        PlayerAction options = new PlayerAction(
            player, needed, currentBetToMatch == 0);
        int committed = 0;
        if (action.equals("check"))
        {
            committed = options.check();
        }
        else if (action.equals("bet"))
        {
            committed = options.bet(amount);
        }
        else if (action.equals("raise"))
        {
            committed = options.raise(amount);
        }
        else if (action.equals("call"))
        {
            // PlayerAction has no call method, so handle matching here
            committed = Math.min(needed, player.getChips());
        }

        if (committed > Integer.MAX_VALUE - pot)
        {
            throw new IllegalArgumentException("The pot would be too large.");
        }
        if (action.equals("fold"))
        {
            options.fold();
        }
        player.setChips(player.getChips() - committed);
        roundBets[index] += committed;
        handBets[index] += committed;
        pot += committed;

        boolean increasedBet = roundBets[index] > currentBetToMatch;
        if (increasedBet)
        {
            currentBetToMatch = roundBets[index];
        }

        turnQueue.dequeue();
        if (!player.isFolded() && player.getChips() > 0)
        {
            turnQueue.enqueue(player);
        }
        if (increasedBet)
        {
            // Everyone else must respond to the new bet, including players
            // who checked or called before this bet was made
            turnsRemaining = turnQueue.size();
            if (player.getChips() > 0)
            {
                turnsRemaining--;
            }
        }
        else
        {
            turnsRemaining--;
        }

        if (turnQueue.isEmpty() || getPlayersStillInHand().size() == 1)
        {
            turnsRemaining = 0;
        }
        else if (turnQueue.size() == 1
            && getAmountToCall(turnQueue.peek()) == 0)
        {
            turnsRemaining = 0;
        }
        return committed;
    }

    /**
     * Returns the current player's available actions
     * @param player the player whose turn it is
     * @return a new list of action names
     */
    public List<String> getLegalActionsFor(Player player)
    {
        requireCurrentPlayer(player);
        ArrayList<String> actions = new ArrayList<String>();
        int needed = getAmountToCall(player);
        if (needed == 0)
        {
            actions.add("check");
            if (currentBetToMatch == 0)
            {
                actions.add("bet");
            }
        }
        else
        {
            actions.add("call");
            if (player.getChips() > needed)
            {
                actions.add("raise");
            }
        }
        actions.add("fold");
        return actions;
    }

    /**
     * CHANGE THIS LATER when we have CopmuterAction
     * @param player the computer whose turn it is
     */
    public void takeComputerTurn(Player player)
    {
        requireCurrentPlayer(player);
        if (player.isHuman())
        {
            throw new IllegalArgumentException("This player is not a computer.");
        }
        if (getAmountToCall(player) == 0)
        {
            applyAction(player, "check", 0);
        }
        else
        {
            applyAction(player, "call", 0);
        }
    }

    /**
     * Ends the hand, pays the pot, and records the human's result
     * 
     *
     * @return players who won a pot, or the best hand(s) if the pot was empty
     */
    public List<Player> showdown()
    {
        List<Player> remaining = getPlayersStillInHand();
        if (!handInProgress || !isBettingRoundOver()
            || (remaining.size() > 1 && communityCards.size() != 5))
        {
            throw new IllegalStateException(
                "Finish betting and deal the full board before showdown.");
        }

        ArrayList<Player> winners = new ArrayList<Player>();
        int[] payouts = new int[players.size()];
        if (remaining.size() == 1)
        {
            winners.add(remaining.get(0));
            payouts[players.indexOf(remaining.get(0))] = pot;
        }
        else if (pot == 0)
        {
            winners.addAll(evaluator.getWinners(remaining, communityCards));
        }
        else
        {

            int previousLevel = 0;
            while (true)
            {
                int nextLevel = 0;
                for (int contribution : handBets)
                {
                    if (contribution > previousLevel
                        && (nextLevel == 0 || contribution < nextLevel))
                    {
                        nextLevel = contribution;
                    }
                }
                if (nextLevel == 0)
                {
                    break;
                }

                int contributors = 0;
                int lastContributor = -1;
                ArrayList<Player> eligible = new ArrayList<Player>();
                for (int i = 0; i < players.size(); i++)
                {
                    if (handBets[i] >= nextLevel)
                    {
                        contributors++;
                        lastContributor = i;
                        if (!players.get(i).isFolded())
                        {
                            eligible.add(players.get(i));
                        }
                    }
                }
                int sidePot = (nextLevel - previousLevel) * contributors;
                if (contributors == 1)
                {
                    payouts[lastContributor] += sidePot;
                }
                else
                {
                    List<Player> tied = evaluator.getWinners(
                        eligible, communityCards);
                    int share = sidePot / tied.size();
                    int extra = sidePot % tied.size();
                    for (int i = 0; i < tied.size(); i++)
                    {
                        Player winner = tied.get(i);
                        int amount = share;
                        if (i < extra)
                        {
                            amount++;
                        }
                        payouts[players.indexOf(winner)] += amount;
                        if (!winners.contains(winner))
                        {
                            winners.add(winner);
                        }
                    }
                }
                previousLevel = nextLevel;
            }
        }

        // Check every payment before changing any player's chip count
        for (int i = 0; i < players.size(); i++)
        {
            if (payouts[i] > Integer.MAX_VALUE - players.get(i).getChips())
            {
                throw new IllegalStateException("A chip count would be too large.");
            }
        }
        for (int i = 0; i < players.size(); i++)
        {
            Player player = players.get(i);
            player.setChips(player.getChips() + payouts[i]);
        }

        Player human = players.get(0);
        int change = human.getChips() - humanStartingChips;
        // A net loss counts as a loss even if the human won a small side pot
        // A zero change tie counts as a win aint no separate tie counter
        if (change > 0 || (change == 0 && winners.contains(human)))
        {
            stats.recordWin(change);
        }
        else
        {
            stats.recordLoss(-change);
        }
        pot = 0;
        currentBetToMatch = 0;
        turnsRemaining = 0;
        handInProgress = false;
        clearTurnQueue();
        return winners;
    }

    public void resetSession()
    {
        resetState();
    }

    private void resetState()
    {
        clearTurnQueue();
        while (!deckQueue.isEmpty())
        {
            deckQueue.dequeue();
        }
        for (int i = 0; i < players.size(); i++)
        {
            Player player = players.get(i);
            player.setChips(startingChips);
            player.clearHand();
            player.setFolded(false);
            roundBets[i] = 0;
            handBets[i] = 0;
            turnQueue.enqueue(player);
        }
        communityCards.clear();
        stats = new SessionStats();
        pot = 0;
        currentBetToMatch = 0;
        turnsRemaining = 0;
        humanStartingChips = startingChips;
        round = -1;
        handInProgress = false;
    }

    public boolean canContinue()
    {
        int fundedPlayers = 0;
        for (Player player : players)
        {
            if (player.getChips() > 0)
            {
                fundedPlayers++;
            }
        }
        return players.get(0).getChips() > 0 && fundedPlayers >= 2;
    }

    public GameSnapshot getSnapshot()
    {
        return new GameSnapshot(players, communityCards, pot);
    }

    public Player getCurrentPlayer()
    {
        if (!handInProgress || isBettingRoundOver())
        {
            return null;
        }
        return turnQueue.peek();
    }

    public boolean isBettingRoundOver()
    {
        return turnsRemaining == 0;
    }

    public boolean isHandInProgress()
    {
        return handInProgress;
    }

    public int getCurrentBet()
    {
        return currentBetToMatch;
    }

    /**
     * Returns the full amount still owed, before limiting it to available chips
     * @param player a player at this table
     * @return the chips needed to match the current bet
     */
    public int getAmountToCall(Player player)
    {
        int index = players.indexOf(player);
        if (index == -1)
        {
            throw new IllegalArgumentException("Player is not at this table.");
        }
        return Math.max(0, currentBetToMatch - roundBets[index]);
    }

    public List<Player> getPlayers()
    {
        return new ArrayList<Player>(players);
    }

    public List<Player> getPlayersStillInHand()
    {
        ArrayList<Player> remaining = new ArrayList<Player>();
        if (handInProgress)
        {
            for (Player player : players)
            {
                if (!player.isFolded())
                {
                    remaining.add(player);
                }
            }
        }
        return remaining;
    }

    public SessionStats getStats()
    {
        return stats;
    }

    private void requireCurrentPlayer(Player player)
    {
        if (player == null || !handInProgress || getCurrentPlayer() != player)
        {
            throw new IllegalArgumentException("It is not this player's turn.");
        }
    }

    private void clearTurnQueue()
    {
        while (!turnQueue.isEmpty())
        {
            turnQueue.dequeue();
        }
    }
}

