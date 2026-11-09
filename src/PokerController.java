import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Runs the console game, reads input, and connects GameHandler to PokerApp.
 * Checks input formatting; the model still enforces all game rules.
 */
public class PokerController
{
    private GameHandler handler;
    private final PokerApp view;
    private final Scanner scanner;
    private boolean quitRequested;

    /**
     * Creates a controller that will ask for names and player count at startup
     * @param view the console view
     */
    public PokerController(PokerApp view)
    {
        this(view, new Scanner(System.in));
    }

    /**
     * Allows a supplied input source, which is useful for testing
     * @param view the console view
     * @param scanner the input source (the controller does not close it)
     */
    public PokerController(PokerApp view, Scanner scanner)
    {
        if (view == null || scanner == null)
        {
            throw new IllegalArgumentException("View and scanner cannot be null.");
        }
        this.view = view;
        this.scanner = scanner;
        quitRequested = false;
    }

    /**
     * Creates a controller for a game that has already been set up
     * @param handler the game model
     * @param view the console view
     */
    public PokerController(GameHandler handler, PokerApp view)
    {
        this(handler, view, new Scanner(System.in));
    }

    /**
     * Creates a controller with an existing model and supplied input source
     * @param handler the game model
     * @param view the console view
     * @param scanner the input source
     */
    public PokerController(GameHandler handler, PokerApp view, Scanner scanner)
    {
        this(view, scanner);
        if (handler == null)
        {
            throw new IllegalArgumentException("GameHandler cannot be null.");
        }
        this.handler = handler;
    }

    public void startGameLoop()
    {
        view.showWelcome();
        if (handler == null)
        {
            setUpGame();
        }
        if (quitRequested)
        {
            view.showMessage("Goodbye!");
            return;
        }

        while (!quitRequested)
        {
            if (!handler.canContinue())
            {
                view.showMessage("This session cannot continue: you need chips"
                    + " and at least one opponent with chips.");
                view.showSessionSummary(handler.getSnapshot());
                if (!promptYesOrNo("Start a fresh session with restored chips? (y/n): "))
                {
                    view.showMessage("Goodbye!");
                    return;
                }
                handler.resetSession();
                view.showMessage("Chips and session results have been reset.");
                view.render(handler.getSnapshot());
            }

            playHand();
            if (quitRequested)
            {
                break;
            }
            if (handler.canContinue() && !promptToContinue())
            {
                break;
            }
        }

        if (handler.isHandInProgress())
        {
            view.showMessage("The current hand was left unfinished. Its pot was"
                + " not paid out, and no result was recorded for it.");
        }
        view.showSessionSummary(handler.getSnapshot());
        view.showMessage("Goodbye!");
    }

    private void setUpGame()
    {
        int count = promptForPlayerCount();
        if (quitRequested)
        {
            return;
        }
        String[] computerNames = {"ComputerA", "ComputerB", "ComputerC",
            "ComputerD", "ComputerE", "ComputerF", "ComputerG"};
        List<String> taken = new ArrayList<String>();
        for (int i = 0; i < count - 1; i++)
        {
            taken.add(computerNames[i]);
        }
        String humanName = promptForName(taken);
        if (quitRequested)
        {
            return;
        }
        List<String> names = new ArrayList<String>();
        names.add(humanName);
        names.addAll(taken);
        handler = new GameHandler(names, 1000);
        view.showMessage("Everyone starts with 1000 chips. Your name is "
            + humanName + ".");
    }

    public void playHand()
    {
        if (handler == null)
        {
            throw new IllegalStateException("Set up the game before dealing.");
        }
        if (quitRequested)
        {
            return;
        }
        handler.dealHand();
        view.showMessage("Starting a new hand.");
        view.render(handler.getSnapshot());

        while (handler.isHandInProgress() && !quitRequested)
        {
            playBettingRound();
            if (quitRequested)
            {
                return;
            }
            GameSnapshot snapshot = handler.getSnapshot();
            int boardSize = snapshot.communityCards.size();
            if (handler.getPlayersStillInHand().size() == 1 || boardSize == 5)
            {
                List<Player> winners = handler.showdown();
                List<String> names = new ArrayList<String>();
                for (Player winner : winners)
                {
                    names.add(winner.getName());
                }
                view.render(handler.getSnapshot());
                view.showWinners(names, snapshot.pot);
            }
            else
            {
                if (boardSize == 0)
                {
                    handler.dealFlop();
                }
                else if (boardSize == 3)
                {
                    handler.dealTurn();
                }
                else if (boardSize == 4)
                {
                    handler.dealRiver();
                }
                view.render(handler.getSnapshot());
            }
        }
    }

    private void playBettingRound()
    {
        while (!handler.isBettingRoundOver() && !quitRequested)
        {
            Player player = handler.getCurrentPlayer();
            if (player.isHuman())
            {
                takeHumanTurn(player);
            }
            else
            {
                int previousChips = player.getChips();
                String action = "call";
                if (handler.getAmountToCall(player) == 0)
                {
                    action = "check";
                }
                handler.takeComputerTurn(player);
                view.showAction(player.getName(), action,
                    previousChips - player.getChips());
            }
            if (!quitRequested)
            {
                view.render(handler.getSnapshot());
            }
        }
    }

    private void takeHumanTurn(Player player)
    {
        String action = promptForAction(player);
        if (quitRequested)
        {
            return;
        }
        int amount = 0;
        if (action.equals("all-in"))
        {
            // Translate the shortcut into an action already allowed by the model.
            List<String> legal = handler.getLegalActionsFor(player);
            if (legal.contains("bet"))
            {
                action = "bet";
                amount = player.getChips();
            }
            else if (legal.contains("raise"))
            {
                action = "raise";
                amount = player.getChips();
            }
            else
            {
                action = "call";
            }
        }
        else if (action.equals("bet") || action.equals("raise"))
        {
            amount = promptForAmount(player);
        }
        if (quitRequested)
        {
            return;
        }
        try
        {
            int committed = handler.applyAction(player, action, amount);
            view.showAction(player.getName(), action, committed);
        }
        catch (IllegalArgumentException exception)
        {
            // The same player stays at the front of the queue to try again.
            view.showError(exception.getMessage());
        }
    }

    /**
     * Asks until the human selects an available action or quits
     * @param player the current player
     * @return an action name
     */
    public String promptForAction(Player player)
    {
        List<String> legal = handler.getLegalActionsFor(player);
        while (!quitRequested)
        {
            view.showPrompt("Choose " + legal + " (or all-in): ");
            String answer = readResponse();
            if (answer == null)
            {
                return null;
            }
            answer = answer.toLowerCase(Locale.ROOT);
            if (legal.contains(answer))
            {
                return answer;
            }
            if ((answer.equals("all-in") || answer.equals("allin"))
                && (legal.contains("bet") || legal.contains("raise")
                    || legal.contains("call")))
            {
                return "all-in";
            }
            view.showError("Choose one of the available actions.");
        }
        return null;
    }
  
    public int promptForAmount(Player player)
    {
        int minimum = handler.getAmountToCall(player) + 1;
        int maximum = player.getChips();
        if (minimum > maximum)
        {
            throw new IllegalStateException("This player cannot bet or raise.");
        }
        while (!quitRequested)
        {
            view.showPrompt("New chips to add (" + minimum + " to " + maximum
                + ", or all-in; this is not your total bet): ");
            String answer = readResponse();
            if (answer == null)
            {
                return -1;
            }
            if (answer.equalsIgnoreCase("all-in") || answer.equalsIgnoreCase("allin"))
            {
                return maximum;
            }
            try
            {
                int amount = Integer.parseInt(answer);
                if (amount >= minimum && amount <= maximum)
                {
                    return amount;
                }
            }
            catch (NumberFormatException exception)
            {
                // The message below also handles text and numbers too large for int.
            }
            view.showError("Enter a whole number from " + minimum + " to " + maximum + ".");
        }
        return -1;
    }

    public int promptForPlayerCount()
    {
        while (!quitRequested)
        {
            view.showPrompt("Total players, including you (2 to 8): ");
            String answer = readResponse();
            if (answer == null)
            {
                return -1;
            }
            try
            {
                int count = Integer.parseInt(answer);
                if (count >= 2 && count <= 8)
                {
                    return count;
                }
            }
            catch (NumberFormatException exception)
            {
                // Invalid text is handled by the message below.
            }
            view.showError("Enter a whole number from 2 to 8.");
        }
        return -1;
    }

    /**
     * Asks for a nonblank name containing only letters and not already in use
     * @param taken names already used at the table
     * @return the name, or null if the user quits
     */
    public String promptForName(List<String> taken)
    {
        while (!quitRequested)
        {
            view.showPrompt("Your name (letters only): ");
            String answer = readResponse();
            if (answer == null)
            {
                return null;
            }
            boolean valid = !answer.isEmpty();
            for (int i = 0; i < answer.length(); i++)
            {
                if (!Character.isLetter(answer.charAt(i)))
                {
                    valid = false;
                }
            }
            for (String name : taken)
            {
                if (name.equalsIgnoreCase(answer))
                {
                    valid = false;
                }
            }
            if (valid)
            {
                return answer;
            }
            view.showError("Use letters only and choose a name not already in use.");
        }
        return null;
    }

    public boolean promptToContinue()
    {
        return promptYesOrNo("Play another hand? (y/n): ");
    }

    private boolean promptYesOrNo(String prompt)
    {
        while (!quitRequested)
        {
            view.showPrompt(prompt);
            String answer = readResponse();
            if (answer == null || answer.equalsIgnoreCase("n")
                || answer.equalsIgnoreCase("no"))
            {
                return false;
            }
            if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes"))
            {
                return true;
            }
            view.showError("Enter y or n.");
        }
        return false;
    }

    private String readResponse()
    {
        if (!scanner.hasNextLine())
        {
            quitRequested = true;
            return null;
        }
        String answer = scanner.nextLine().trim();
        if (answer.equalsIgnoreCase("quit") || answer.equalsIgnoreCase("q"))
        {
            quitRequested = true;
            return null;
        }
        return answer;
    }
}

