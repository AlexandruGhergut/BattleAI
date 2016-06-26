package Security;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dragos-Alexandru
 */
public class GuardTest {

    public GuardTest() {
    }

    /**
     * Test of scramblePassword method, of class Guard.
     */
    @Test
    public void testScramblePassword() {
        System.out.println("scramblePassword");
        String input = "adfdasdaw";
        String input2 = "adfdasdaw";
        String input3 = "bcvzxczx";
        String expResult = Guard.scramblePassword(input);
        String result = Guard.scramblePassword(input2);
        assertEquals(expResult, result);
        result = Guard.scramblePassword(input3);
        assertNotSame(expResult, result);
    }

}
