package Networking;

import Networking.Server.ClientServerDispatcher;
import Networking.Server.Match;
import Networking.Server.ServerDispatcher;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class NetworkTest {
    
    public NetworkTest() throws InterruptedException {
        ServerDispatcher.getInstance().start(Constants.MasterServerConstants.PORT);
        Thread.sleep(1500);
    }
    
    @Test
    public void masterServerListingTest() throws InterruptedException, IOException {
        Match match = new Match("Test", "127.0.0.1",
                Constants.MasterServerConstants.PORT + 1, "test", 2);
        ClientServerDispatcher.getInstance().start(match);
        Thread.sleep(1500);
        List<Match> activeMatches = 
                ServerDispatcher.getInstance().getActiveMatches();
        assertEquals(match, activeMatches.get(0));
    }
    
    @AfterClass
    public static void stopServers() {
        ClientServerDispatcher.getInstance().stop();
        ServerDispatcher.getInstance().stop();
    }
}