package Database;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author root
 */
public class ConnectionTest {
    public ConnectionTest() {
    }
    
    @Test
    public void connect() {
        boolean result = DatabaseHandler.getInstance("root", "").testConnection();
        assertEquals(result, true);
    }
}
