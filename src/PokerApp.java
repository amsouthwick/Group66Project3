import java.util.List;

/**
 * The console view. Prints snapshots, prompts, messages, and results.
 * Does not read input, decide legal actions, or change the game.
 */
public class PokerApp
{
    /**
     * Starts the console application
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args)
    {
        PokerApp view = new PokerApp();
        PokerController controller = new PokerController(view);
        controller.startGameLoop();
    }

    public void showWelcome()
    {
        System.out.println("Welcome to Casino Practice - Texas Hold'em!");
        System.out.println("Computers check or call. There are no blinds or antes.");
        System.out.println("Any positive opening bet or increase is allowed.");
        System.out.println("Type quit at any prompt to stop playing.");
    }

    public void render(GameSnapshot snapshot)
    {
        System.out.println();
        System.out.println("=== " + snapshot.roundName + " ===");
        System.out.println("Board: " + snapshot.communityCards);
        System.out.println("Pot: " + snapshot.pot
            + " | Current bet: " + snapshot.currentBet);
        for (int i = 0; i < snapshot.playerNames.size(); i++)
        {
            System.out.println(snapshot.playerNames.get(i) + ": "
                + snapshot.chipCounts.get(i) + " chips | Round bet: "
                + snapshot.roundBets.get(i) + " | "
                + snapshot.playerStatuses.get(i) + " | Cards: "
                + snapshot.visibleHands.get(i));
        }
        if (!snapshot.currentPlayerName.isEmpty())
        {
            System.out.println("Next turn: " + snapshot.currentPlayerName);
        }
        if (snapshot.humanTurn)
        {
            System.out.println("Amount to match: " + snapshot.amountToCall);
            System.out.println("Available actions: " + snapshot.legalActions);
        }
    }

    public void showPrompt(String prompt)
    {
        System.out.print(prompt);
        System.out.flush();
    }

    /**
     * Prints an informational message
     * @param message text to display
     */
    public void showMessage(String message)
    {
        System.out.println(message);
    }

    /**
     * Prints an input error from the controller or model
     * @param message explanation of the problem
     */
    public void showError(String message)
    {
        System.out.println("Please try again: " + message);
    }

    /**
     * Describes a completed move
     * @param name the player's name
     * @param action the action taken
     * @param committed new chips added to the pot
     */
    public void showAction(String name, String action, int committed)
    {
        if (action.equals("check"))
        {
            showMessage(name + " checked.");
        }
        else if (action.equals("fold"))
        {
            showMessage(name + " folded.");
        }
        else
        {
            showMessage(name + " chose " + action + " and added "
                + committed + " chips to the pot.");
        }
    }

    /**
     * Displays the winners already chosen by the model
     * @param names winner names, including winners of separate side pots
     * @param previousPot pot size before the hand was paid out
     */
    public void showWinners(List<String> names, int previousPot)
    {
        if (previousPot == 0)
        {
            showMessage("Hand winner(s): " + names + ". No chips were wagered.");
        }
        else
        {
            showMessage("Hand winner(s): " + names);
            showMessage("Pot settled: " + previousPot + " chips.");
        }
    }

    /**
     * Displays results for completed hands in the current session
     * @param snapshot a snapshot containing the session results
     */
    public void showSessionSummary(GameSnapshot snapshot)
    {
        System.out.println();
        System.out.println("=== Session results ===");
        System.out.println("Wins: " + snapshot.wins);
        System.out.println("Losses: " + snapshot.losses);
        System.out.println("Net chip gain/loss: " + snapshot.totalGain);
    }
}

