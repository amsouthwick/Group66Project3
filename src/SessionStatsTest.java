import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SessionStatsTest
{
    private SessionStat test1;

    @BeforeAll
    static void setUp()
    {
        test1 = new SessionStat();
    }
    // ~ Fields ................................................................


    @Test
    void testRecordWin()
    {
        test1.recordWin(100);
        assertEquals(1, test1.getWins());
        assertEquals(100, test1.getTotalGains());
    }
    
    @Test
    void testRecordLoss()
    {
        test1.recordLoss(75);
        assertEquals(1, test1.getLosses());
        assertEquals(25, test1.getTotalGains());
    }
    
    @Test
    void testGetters()
    {
        
        assertEquals(1, test1.getWins());
        assertEquals(100, test1.getTotalGains());
        assertEquals(1, test1.getLosses());
        
    }

    // ~ Constructors ..........................................................

    // ~Public Methods ........................................................

}
