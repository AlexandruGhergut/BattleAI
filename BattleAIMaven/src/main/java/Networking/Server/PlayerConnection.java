package Networking.Server;

import Console.ConsoleFrame;
import Networking.Client.ConnectionHandler;
import Constants.MasterServerConstants;
import Networking.Requests.PlayerConnect;
import Networking.Requests.Request;
import Networking.Requests.RequestType;
import Networking.Requests.SourceFileTransfer;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * PlayerConnection handles the continuous connection between a player and a
 * match. The match requires every player to send requests each PACKET_DELAY
 * milliseconds in order to check if the connection is still active. This class
 * starts a thread the constructor running its own run method in order to read
 * and handle requests. The connection is deemed inactive if a period of
 * PACKET_DELAY * 2 milliseconds have passed and no request has been received!
 */
public class PlayerConnection extends Connection {

    private String username;
    private boolean identityConfirmed;

    public PlayerConnection(Socket clientSocket) throws IOException {
        super(clientSocket);
        username = "Anonymous";
        identityConfirmed = false;
    }

    private void startConnectionHandler() {
        Timer connectionHandler = new Timer();

        /* This task runs every PACKET_DELAY * 2 milliseconds. If 
        activeConnection is true, then a request has been received in the last 
        PACKET_DELAY * 2 milliseconds and we set activeConnection to false. 
        activeConnections becomes true after a request has been received. If 
        activeConnection is false when this task is executed, it means a request
        has not been received in the last PACKET_DELAY * 2 milliseconds and 
        the connection is inactive for good.
         */
        TimerTask handleConnections = new TimerTask() {
            @Override
            public void run() {
                if (!threadRunning.get()) {
                    connectionHandler.cancel();
                    return;
                }

                int level = inactivityLevel.incrementAndGet();

                if (level == MAX_INACTIVITY_LEVEL) {
                    closeConnection();
                }

            }
        };
        connectionHandler.scheduleAtFixedRate(handleConnections, MasterServerConstants.PACKET_DELAY * 2,
                MasterServerConstants.PACKET_DELAY * 2);
    }

    @Override
    public void run() {
        threadRunning.set(true);
        startConnectionHandler();

        Object object;

        while (threadRunning.get()) {
            try {
                if (!clientSocket.isInputShutdown()) {
                    object = inputStream.readObject();

                    // decrease level by 1 but remain non-negative
                    inactivityLevel.updateAndGet(i -> i > 0 ? i - 1 : i);

                    Request request = (Request) object;
                    request.execute(outputStream);

                    if (request.getType() == RequestType.SOURCE_FILE_TRANSFER) {
                        // map this source to the player
                        SourceFileTransfer source = (SourceFileTransfer) request;
                        if (source.isRemovingSource()) {
                            ClientServerDispatcher.getInstance().getSourceFilesMap().remove(username);
                        } else {
                            ClientServerDispatcher.getInstance().getSourceFilesMap().put(username, source.getSource());
                        }
                    } else if (!identityConfirmed && request.getType() == RequestType.PLAYER_CONNECT) {
                        identityConfirmed = true;
                        username = ((PlayerConnect) request).getUsername();
                    }
                }

            } catch (IOException | ClassNotFoundException ex) {
                //Logger.getLogger(MatchConnection.class.getName()).log(Level.SEVERE, null, ex);
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), ex.getMessage());
                closeConnection();
            }
        }

        // remove source mapping
        ClientServerDispatcher.getInstance().getSourceFilesMap().remove(username);

        try {
            ConnectionHandler.getInstance().sendToMatch(new PlayerConnect(username, true));
        } catch (IOException ex) {
            //Logger.getLogger(PlayerConnection.class.getName()).log(Level.SEVERE, null, ex);
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public String getUsername() {
        return username;
    }
}
