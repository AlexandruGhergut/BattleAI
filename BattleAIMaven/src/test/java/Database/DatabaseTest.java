package Database;

import java.sql.SQLException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author root
 */
public class DatabaseTest {
    private final String[] tables;
    
    public DatabaseTest() {
        DatabaseHandler.getInstance();
        tables = new String[]{"PLAYER_DB, MATCHES_DB, ATTEND"};
    }
    
    @Test
    public void connectionTest() throws SQLException {
        boolean result = DatabaseHandler.getInstance().testConnection();
        assertEquals(result, true);
    }
    
    @Test
    public void tableCreationTest() {
        for (String tableName: tables)
            assertEquals(DatabaseHandler.getInstance().tableExists(tableName), true);
    }
    
    @Test
    public void sqlInjectionTest1() {
        String username = "johnny';DROP TABLE PLAYER_DB;--";
        InfoPlayer.isValidAccount(username, "abcd");
        assertEquals(DatabaseHandler.getInstance().tableExists("PLAYER_DB"), true);
    }
    
    @Test
    public void sqlInjectionTest2() {
        String username = "johnny' OR 1=1/*";
        assertEquals(InfoPlayer.isValidName(username), true);
        
    }
    
    @Test
    public void sqlInjectionTest3() {
        String username = "'=''/*";
        assertEquals(InfoPlayer.isValidName(username), true);
    }
    
    @Test
    public void sqlInjectionTest4() {
        String username = "$badboypwnsyodatabase$ >:)";
        String password = "(SELECT 1 FROM DUAL)";
        InfoPlayer.signUp(username, password);
        assertEquals(InfoPlayer.isValidAccount(username, "1"), false);
        assertEquals(InfoPlayer.isValidAccount(username, password), true);
        InfoPlayer.removeAccount(username, password);
        assertEquals(InfoPlayer.isValidAccount(username, password), false);
    }
}
