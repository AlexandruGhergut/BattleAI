package Networking.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import Networking.Requests.HostMatch;
import Networking.Requests.Request;
import Networking.Requests.RequestType;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 
 * <pre>RegularConnection handles the interaction between clients who do not currently
 * host a match and the master server. These interactions involve registering,
 * logging in, requesting the match list etc. This type of connection is not 
 * continuous, meaning that it doesn't require a continuous stream of packet activity
 * and it closes itself when inactivity level reaches maximum. Inactivity level
 * decreases by one once a request is made but remains non-negative.
 * If a HostMatch request is made, this type of connection closes immediately
 * and a MatchConnection using current socket and existing streams is created.
 * </pre>
 */
public class RegularConnection extends Connection {
    
    /* variable used to indicate whether this connection has been switched to
    a MatchConnection */
    private boolean switchedConnection;
    
    /**
     * @param clientSocket The client socket associated with the connection.
     */
    public RegularConnection(Socket clientSocket) throws IOException {
        super(clientSocket);
        this.start();
        switchedConnection = false;
    }
    
    /**
     * Starts the main thread
     */
    private void start() {
        threadRunning = true;
        new Thread(this).start();
    }
    
    /**
     * Starts the connection handler used to count how many time this
     * connection has left and close it once that time expires.
     */
    private void startConnectionHandler() {
        Timer connectionHandler = new Timer();
        
        TimerTask handleConnections = new TimerTask() {
            @Override
            public void run() {
                // if switched, close this handle
                if (switchedConnection)
                {
                    connectionHandler.cancel();
                    return;
                }
                
                // close connection if inactivity level has reached maximum
                if (inactivityLevel.get() == MAX_INACTIVITY_LEVEL) {
                    threadRunning = false;
                    activeConnection = false;
                    connectionHandler.cancel();
                    
                    try {
                        clientSocket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(RegularConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                    inactivityLevel.incrementAndGet();
            }
        };
        
        connectionHandler.scheduleAtFixedRate(handleConnections, 30 * 1000, 
                30 * 1000);
    }
    
    @Override
    public void run() {
        startConnectionHandler();
        
        Object object = null;
        
        while (threadRunning) {
            try {

                if (!clientSocket.isInputShutdown()) {
                    object = inputStream.readObject();
                    
                    // decrease inactivity level 
                    inactivityLevel.updateAndGet(i -> i > 0 ? i - 1 : i);
                    
                    Request request = (Request)object;
                    request.execute(outputStream);
                    
                    // switch the connection if HostMatch request is made
                    if (request.getType() == RequestType.HOST_MATCH) {
                        Match match = ((HostMatch)request).getMatch();
                        switchedConnection = true;
                        ServerDispatcher.getInstance().addConnection(new MatchConnection(clientSocket, inputStream, outputStream, match));
                        threadRunning = false;
                        activeConnection = false;
                    }
                }
            } catch (ClassNotFoundException | IOException ex) {
                Logger.getLogger(MatchConnection.class.getName()).log(Level.SEVERE, null, ex);
                threadRunning = false;
                activeConnection = false;
            }
        }
    }
}
