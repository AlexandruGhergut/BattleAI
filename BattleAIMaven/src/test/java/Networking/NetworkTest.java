package Networking;

import Networking.Client.ConnectionHandler;
import Networking.Server.ClientServerDispatcher;
import Networking.Server.Match;
import Networking.Server.ServerDispatcher;
import java.io.IOException;
import java.util.List;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class NetworkTest {
    
    public NetworkTest() throws InterruptedException {
        System.setProperty("TEST", "true");
        ServerDispatcher.getInstance().start(Constants.MasterServerConstants.PORT);
        Thread.sleep(1500);
    }
    
    @Test
    public void masterServerListingTest() throws InterruptedException, IOException {
        Match match = new Match("Test", "127.0.0.1",
                Constants.MasterServerConstants.PORT + 1, "test", 2);
        ConnectionHandler.getInstance().hostMatch(match);
        Thread.sleep(1500);
        List<Match> activeMatches = 
                ServerDispatcher.getInstance().getActiveMatches();
        assertEquals(activeMatches.size(), 1);
        assertEquals(match, activeMatches.get(0));
    }
    
    @AfterClass
    public static void stopServers() {
        ConnectionHandler.getInstance().disconnectFromMatch();
        ClientServerDispatcher.getInstance().stop();
        ServerDispatcher.getInstance().stop();
    }
}
