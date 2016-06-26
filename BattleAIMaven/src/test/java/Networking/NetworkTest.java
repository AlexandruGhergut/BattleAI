package Networking;

import Networking.Client.ConnectionHandler;
import Networking.Requests.ChatMessage;
import Networking.Requests.GetMatchList;
import Networking.Server.ClientServerDispatcher;
import Networking.Server.Match;
import Networking.Server.ServerDispatcher;
import java.io.IOException;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class NetworkTest {
    
    private final int sleepAmount;
    private final Match activeMatch;
    
    public NetworkTest() {
        System.setProperty("TEST", "true");
        sleepAmount = 1500;
        activeMatch =  new Match("Test", "127.0.0.1",
                Constants.MasterServerConstants.PORT + 1, "test", 2);
    }
    
    @Before
    public void startServers() throws InterruptedException {
        ServerDispatcher.getInstance().start(Constants.MasterServerConstants.PORT);
        Thread.sleep(sleepAmount);
        ConnectionHandler.getInstance().hostMatch(activeMatch);
        Thread.sleep(sleepAmount);
    }
    
    @After
    public void stopServers() {
        ConnectionHandler.getInstance().disconnectFromMatch();
        ClientServerDispatcher.getInstance().stop();
        ServerDispatcher.getInstance().stop();
    }
    
    @Test
    public void masterServerListingTest() throws InterruptedException, IOException, ClassNotFoundException {
        List<Match> activeMatches
                = (List<Match>)ConnectionHandler.getInstance().readFromMasterServer(new GetMatchList());
        assertEquals(activeMatches.size(), 1);
        assertEquals(activeMatch, activeMatches.get(0));
    }
    
    @Test(expected=IOException.class)
    public void masterServerDownTest() throws IOException, ClassNotFoundException {
        ServerDispatcher.getInstance().stop();
        ConnectionHandler.getInstance().readFromMasterServer(new GetMatchList());
    }
    
    @Test(expected=IOException.class)
    public void matchServerDownTest() throws IOException, ClassNotFoundException {
        ClientServerDispatcher.getInstance().stop();
        ConnectionHandler.getInstance().sendToMatch(new ChatMessage("Hello"));
        ConnectionHandler.getInstance().readFromMatch();
    }
}
