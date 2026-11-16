package BlackJack;

import java.util.Scanner;

/**
 * Runs the blackjack game: betting, dealing, turns, and payouts.
 */
public class Game
{
    private Deck deck;
    private Player player;
    private Dealer dealer;
    private Scanner input;
    /**
     * Creates a new game.
     *
     * @param playerName the player's name
     * @param startingChips the player's starting chips
     */
    public Game(String playerName, int startingChips) {
        deck = new Deck();
        player = new Player(playerName, startingChips);
        dealer = new Dealer("Dealer", 0);
        input = new Scanner(System.in);
    }
    /**
     * Plays rounds until the player runs out of chips or quits.
     */
    public void play() {
        System.out.println("Welcome to Blackjack, " + player.getName() + "!");
        boolean keepPlaying = true;
        while (keepPlaying && player.getChips() > 0 ) {
            playRound();
            
            if(player.getChips() > 0) {
                System.out.print("\nPlay another round? (y/n): ");
                String answer = input.nextLine().trim().toLowerCase();
                keepPlaying = answer.equals("y");
            }
        }
        
        if (player.getChips() <= 0) {
            System.out.println("\nYou're out of chips. Game over!");
        }
        else {
            System.out.println("\nThanks for playing! You leave with "
                + player.getChips() + " chips.");
        }
    }
    /**
     * Plays a single round from bet to payout.
     */
    public void playRound() {
        player.resetHand();
        dealer.resetHand();
        
        takeBets();
        dealInitialCards();
        
        boolean playerBlackjack = player.getHand().Blackjack();
        boolean dealerBlackjack = dealer.getHand().Blackjack();
        
        if (playerBlackjack || dealerBlackjack) {
            showAllHands();
            settleBlackJackWins(playerBlackjack, dealerBlackjack);
            return;
        }
        
        playersTurn();
        
        if (player.getHand().Bust()) {
            System.out.println("\n" + player.getHand());
            System.out.println("You bust! You lose " + player.getBet() + " chips.");
            player.lose();
            return;
        }
        System.out.println("\nDealer's turn...");
        dealer.playTurn(deck);
        showAllHands();
        settleRound();
    }
    /**
     * Asks the player for a valid bet.
     */
    private void takeBets() {
        boolean validation = false;
        while (!validation) {
            System.out.println("\nChips: " + player.getChips());
            System.out.print("Place your bet: ");
            String text = input.nextLine().trim();
            if (text.matches("\\d{1,9}")) {
                int amount = Integer.parseInt(text);
                validation = player.placeBet(amount);
                if (!validation) {
                    System.out.println("Bet must be between 1 and "
                        + player.getChips() + ".");
                }
            }else {
                System.out.println("Please enter a whole number.");
            }
        }  
    }
    /**
     * Deals two cards to the player and two to the dealer.
     */
    private void dealInitialCards() {
        player.recieveCard(deck.deals());
        dealer.recieveCard(deck.deals());
        player.recieveCard(deck.deals());
        dealer.recieveCard(deck.deals());
    }
    /**
     * Lets the player hit or stand until they can't act anymore.
     */
    private void playersTurn() {
        while (player.actions()) {
            System.out.println("\nDealer shows: " + dealer.getHand().getCard(0));
            System.out.println(player);
            System.out.print("Hit or stand? (h/s): ");
            String choice = input.nextLine().trim().toLowerCase();
            
            if (choice.equals("h")) {
                player.recieveCard(deck.deals());
            } 
            else if (choice.equals("s")) {
                player.stand();
            }
            else {
                System.out.println("Please type h or s");
            }
            
            
        }
    }
    /**
     * Handles the case where someone has a natural blackjack.
     */
    private void settleBlackJackWins(boolean playerBlackjack, boolean dealerBlackjack) {
        if (playerBlackjack && dealerBlackjack) {
            System.out.println("Both have blackjack. Push!");
            player.push();
        }
        else if (playerBlackjack) {
            System.out.println("Blackjack! You win 3:2.");
            player.blackJackWin();
        }
        else {
            System.out.println("Dealer has blackjack. You lose.");
            player.lose();
        }
    }
    /**
     * Compares totals after the dealer finishes and pays out.
     */
    private void settleRound() {
        int playerTotal = player.getHand().getTotal();
        int dealerTotal = dealer.getHand().getTotal();
        
        if(dealer.getHand().Bust()) {
            System.out.println("Dealer busts! You win!");
            player.win();
        }
        else if (playerTotal > dealerTotal) {
            System.out.println("You win!");
            player.win();
        }
        else if (playerTotal < dealerTotal) {
            System.out.println("Dealer wins. You lose!");
            player.lose();
        }
        else {
            System.out.println("Push! Your bet is returned.");
            player.push();
        }
    }
    /**
     * Prints both full hands.
     */
    private void showAllHands() {
        System.out.println("\n" + player);
        System.out.println(dealer);
    }
}
