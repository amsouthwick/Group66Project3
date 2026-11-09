// -------------------------------------------------------------------------
/**
 *  keeps track of all win, losses, and chip gain
 * 
 *  @author micha
 *  @version Nov 9, 2026
 */
public class SessionStats
{
    // ~ Fields ................................................................
    private int wins;
    private int losses;
    private int totalGain;

    // ----------------------------------------------------------
    /**
     * sets wins losses and total gain to zero
     */
    public SessionStats()
    {
        wins = 0;
        losses = 0;
        totalGain = 0;
    }


    // ----------------------------------------------------------
    /**
     * adds a win and ads the amount of chips won
     * 
     * @param gain
     */
    public void recordWin(int gain)
    {
        wins++;
        totalGain = totalGain + gain;
    }


    // ----------------------------------------------------------
    /**
     * adds a loss and removes lost chips from total gain
     * @param loss 
     */
    public void recordLoss(int loss)
    {
        losses++;
        totalGain = totalGain - loss;
    }


    // ----------------------------------------------------------
    /**
     * returns the number of wins
     * @return wins
     */
    public int getWins() {
        return wins;
    }
    
    // ----------------------------------------------------------
    /**
     * returns the number of losses
     * @return losses
     */
    public int getlosses() {
        return losses;
    }
    
    // ----------------------------------------------------------
    /**
     * returns the net value of chips
     * @return TotalGain
     */
    public int getTotalGain() {
        return totalGain;
    }

}
