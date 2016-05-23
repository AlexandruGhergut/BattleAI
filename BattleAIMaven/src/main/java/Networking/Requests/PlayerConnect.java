package Networking.Requests;

import Networking.Client.ConnectionHandler;
import Networking.Server.ClientServerDispatcher;
import Networking.Server.Match;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This request is sent when attempting to connect to a match.
 */
public class PlayerConnect extends Request {

    private String username;
    private boolean isDisconnecting;
    
    public PlayerConnect(String username) {
        super(RequestType.PLAYER_CONNECT);
        this.username = username;
        isDisconnecting = false;
    }
    
    public PlayerConnect(String username, boolean isDisconnecting) {
        this(username);
        this.isDisconnecting = isDisconnecting;
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {
        try {
            Request request = isDisconnecting ? new RemovePlayer(username) : new AddPlayer(username);
            String message = username;
            Match activeMatch = ClientServerDispatcher.getInstance().getActiveMatch();
            if (isDisconnecting) {
                message += " has left.\n";
                activeMatch.removePlayer(username);
            }
            else {
                message += " has joined.\n";
                activeMatch.addPlayer(username);
            }
            // Ask master server to update match
            ConnectionHandler.getInstance().sendToMasterServer(request);
            // Broadcast request to all connected clients
            ClientServerDispatcher.getInstance().broadcast(request);
            // Broadcast join/left message
            ClientServerDispatcher.getInstance().broadcast(new ChatMessage(message));
        } catch (IOException ex) {
            Logger.getLogger(PlayerConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getUsername() {
        return username;
    }
}
