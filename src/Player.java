


public class Player
{
    public enum PlayerType {
        HUMAN,
        BOT
    }
    
    //~ Fields ................................................................
    private ArrayList<Card> hand;
    private String name;
    private PlayerType type;
    private boolean fold;
    
    //~ Constructors ..........................................................
    public Player(String name, PlayerType type) {
        this.name = name;
        this.type = type;
        this.fold = false;
    }

    //~Public  Methods ........................................................
     public int currentBet() {
         // TODO 
     }
     
     public String getHand() {
         return hand.toString();
      
     }
     
     public boolean isHuman() {
         return type == PlayerType.HUMAN;
     }
     
     public void fold() {
         this.fold = true;
     }
     
     public boolean folded() {
         return fold;
     }
     
     public void reset() {
         fold = false;
         for(int i = 0; i < 2; i++) {
             hand[i] = null;
         }
     }
}
