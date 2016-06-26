package Networking.Client;

import Console.ConsoleFrame;
import Constants.MasterServerConstants;
import Interface.MainFrame;
import Interface.MultiplayerLanPanel;
import Interface.MultiplayerServerPanel;
import Networking.Requests.GetPlayerStateList;
import Networking.Requests.PlayerConnect;
import Networking.Requests.RegisterActivity;
import Networking.Server.Match;
import Networking.Requests.Request;
import Networking.Server.ClientServerDispatcher;
import Networking.Server.Player;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles two type of connections, the connection to the master
 * server and the connection to a match.
 */
public class ConnectionHandler {

    private final static ConnectionHandler INSTANCE = new ConnectionHandler();

    private final InetSocketAddress masterServerAddress;
    private Socket masterServerSocket;
    private ObjectInputStream masterServerInputStream;
    private ObjectOutputStream masterServerOutputStream;

    private Socket matchSocket;
    private ObjectInputStream matchInputStream;
    private ObjectOutputStream matchOutputStream;

    private boolean host;
    // this variable indicated where the disconnect was voluntary
    private boolean disconnectedFromMatch;

    private final BlockingQueue<Request> gameDataQueue;

    public boolean isHost() {
        return host;
    }

    private ConnectionHandler() {
        masterServerAddress
                = new InetSocketAddress(MasterServerConstants.IP, MasterServerConstants.PORT);
        gameDataQueue = new LinkedBlockingQueue<>();
        masterServerSocket = null;
        matchSocket = null;
        host = false;
        disconnectedFromMatch = false;
        matchSocket = null;
    }

    ;
    
    public static ConnectionHandler getInstance() {
        return INSTANCE;
    }

    private void connectToMasterServer() throws IOException {
        try {
            masterServerSocket = new Socket();
            masterServerSocket.setTcpNoDelay(MasterServerConstants.TCP_NO_DELAY);
            masterServerSocket.connect(masterServerAddress, 3500);
            masterServerSocket.setSoTimeout(5000);
            masterServerOutputStream = new ObjectOutputStream(masterServerSocket.getOutputStream());
            masterServerOutputStream.flush();
            masterServerInputStream = new ObjectInputStream(masterServerSocket.getInputStream());
        } catch (IOException ex) {
            masterServerSocket = null;
            throw ex;
        }

    }

    public boolean hostMatch(Match activeMatch) {
        if (!host) {
            if (ClientServerDispatcher.getInstance().start(activeMatch)) {
                try {
                    host = true;
                    connectToMatch(activeMatch);
                } catch (IOException ex) {
                    Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return host;
    }

    /**
     * Attempts to send a request to master server.
     *
     * @param request Request send to the master server.
     * @throws IOException
     */
    public synchronized void sendToMasterServer(Request request) throws IOException {
        if (masterServerSocket == null) {
            connectToMasterServer();
        }

        try {
            masterServerOutputStream.writeObject(request);
            masterServerOutputStream.flush();
        } catch (IOException ex) {
            try {
                connectToMasterServer();
                masterServerOutputStream.writeObject(request);
                masterServerOutputStream.flush();
            } catch (IOException ex2) {
                masterServerSocket = null;
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), ex2.getMessage());
                throw ex2;
            }
        }
    }

    /**
     * Sends an request to the master-server and returns the response. This
     * should be used only with requests that ask for a response.
     *
     * @param request Request object to which you're expecting a response
     * @return Object read from master server.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object readFromMasterServer(Request request) throws IOException, ClassNotFoundException {
        if (masterServerSocket == null) {
            connectToMasterServer();
        }

        Object response = null;
        try {
            masterServerOutputStream.writeObject(request);
            masterServerOutputStream.flush();
            response = masterServerInputStream.readObject();
        } catch (IOException ex) {
            try {
                connectToMasterServer();
                masterServerOutputStream.writeObject(request);
                masterServerOutputStream.flush();
                response = masterServerInputStream.readObject();
            } catch (IOException ex2) {
                Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex2);
                masterServerSocket = null;
                throw ex2;
            }
        }
        return response;
    }

    public void connectToMatch(String ip, int port) throws IOException {
        connectToMatch(new Match("Match", ip, port, "", 0));
    }

    public void connectToMatch(Match match) throws IOException {
        InetSocketAddress address = new InetSocketAddress(match.getIP(), match.getPort());
        System.out.println("Connecting to " + match.getIP());
        matchSocket = new Socket();
        matchSocket.setTcpNoDelay(MasterServerConstants.TCP_NO_DELAY);
        matchSocket.connect(address, 3500);

        matchOutputStream = new ObjectOutputStream(matchSocket.getOutputStream());
        matchOutputStream.flush();
        matchInputStream = new ObjectInputStream(matchSocket.getInputStream());
        matchOutputStream.writeObject(new GetPlayerStateList());
        matchOutputStream.flush();
        matchOutputStream.writeObject(new PlayerConnect(Player.getInstance().getUsername()));
        matchOutputStream.flush();

        Timer t = new Timer();
        TimerTask notification = new TimerTask() {
            @Override
            public void run() {
                try {
                    matchOutputStream.writeObject(new RegisterActivity());
                    matchOutputStream.flush();
                } catch (IOException ex) {
                    t.cancel();

                    if (!host && !disconnectedFromMatch) {
                        if (Player.getInstance().isLoggedIn()) {
                            MainFrame.getInstance()
                                    .changePanel(new MultiplayerServerPanel(MainFrame.getInstance()));
                        } else {
                            MainFrame.getInstance()
                                    .changePanel(new MultiplayerLanPanel(MainFrame.getInstance()));
                        }
                        ConsoleFrame.showError("Connection lost.");
                    } else {
                        host = false;
                        disconnectedFromMatch = false;
                    }
                }
                System.out.println("Send acknowledgement");
            }
        };
        t.scheduleAtFixedRate(notification, 0, MasterServerConstants.PACKET_DELAY);
    }

    public void disconnectFromMatch() {
        try {
            matchSocket.close();
            disconnectedFromMatch = true;
            if (host) {
                ClientServerDispatcher.getInstance().stop();
                /* the server does not disappear almost immediately from the 
                   server-browser after it is closed. The workaround is to close
                   the master-server socket when closing the server. The issue
                   should be further investigated */
                if (masterServerSocket != null) {
                    masterServerSocket.close();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Object readFromMatch() throws IOException, ClassNotFoundException {
        Object object = matchInputStream.readObject();
        return object;
    }

    public void sendToMatch(Request request) throws IOException {
        matchOutputStream.reset();
        matchOutputStream.writeObject(request);
        matchOutputStream.flush();
    }

    public void addGameData(Request request) {
        gameDataQueue.add(request);
    }

    public Request getGameData() throws InterruptedException {
        return gameDataQueue.take();
    }

    public void clearGameData() {
        gameDataQueue.clear();
    }
}
