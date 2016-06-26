package Networking.Requests;

import Console.ConsoleFrame;
import Networking.Client.ConnectionHandler;
import Networking.Server.ClientServerDispatcher;
import Networking.Server.Match;
import java.io.IOException;
import java.io.ObjectOutputStream;

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
        Request request = isDisconnecting ? new RemovePlayer(username) : new AddPlayer(username);
        String message = username;
        Match activeMatch = ClientServerDispatcher.getInstance().getActiveMatch();
        if (isDisconnecting) {
            message += " has left.\n";
            activeMatch.removePlayer(username);
        } else {
            message += " has joined.\n";
            activeMatch.addPlayer(username);
        }
        try {
            // Ask master server to update match
            ConnectionHandler.getInstance().sendToMasterServer(request);
        } catch (IOException ex) {
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to contact master-server.");
        }
        // Broadcast request to all connected clients
        ClientServerDispatcher.getInstance().broadcast(request);
        // Broadcast join/left message
        ClientServerDispatcher.getInstance().broadcast(new ChatMessage(message));
    }

    public String getUsername() {
        return username;
    }
}
