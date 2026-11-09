import java.util.ArrayList;

// -------------------------------------------------------------------------
/**
 *  a class that defines what the players can do
 * 
 *  @author micha
 *  @version Nov 9, 2026
 */
public class Player
{
    // -------------------------------------------------------------------------
    /**
     *  an enum of the two types of players
     * 
     *  @author micha
     *  @version Nov 9, 2026
     */
    public enum PlayerType
    {
        /**
         * the user
         */
        HUMAN,
        /**
         * the other players
         */
        BOT
    }

    // ~ Fields ................................................................
    private ArrayList<Card> hand;
    private String name;
    private PlayerType type;
    private boolean fold;
    private int chipValue;

    // ~ Constructors ..........................................................
    // ----------------------------------------------------------
    /**
     * Create a new Player object.
     * 
     * @param name
     * @param type
     */
    public Player(String name, PlayerType type)
    {
        this.name = name;
        this.type = type;
        this.fold = false;
        this.chipValue = 1000;
    }


    // ~Public Methods ........................................................
//    public int currentBet(int bet)
//    {
//        // TODO check bets
//        if (bet > chipValue)
//        {
//            
//        }
//
//    }


    // ----------------------------------------------------------
    /**
     * returns the name of player
     * 
     * @return name of player
     */
    public String getName()
    {
        return name;
    }


    // ----------------------------------------------------------
    /**
     *returns the chip value the player has
     * @return chipValue
     */
    public int getChips()
    {
        return chipValue;
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     * @return the cards in the player hands
     */
    public String getHand()
    {
        return hand.toString();

    }


    // ----------------------------------------------------------
    /**
     * returns weather the player is the user
     * @return if player is human
     */
    public boolean isHuman()
    {
        return type == PlayerType.HUMAN;
    }


    // ----------------------------------------------------------
    /**
     * lets the player fold
     */
    public void fold()
    {
        this.fold = true;
    }


    // ----------------------------------------------------------
    /**
     * checks if player has folded
     * @return fold
     */
    public boolean folded()
    {
        return fold;
    }

}
