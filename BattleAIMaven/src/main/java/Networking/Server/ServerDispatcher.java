package Networking.Server;

import Console.ConsoleFrame;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Constants.MasterServerConstants;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerDispatcher implements Runnable {

    private static final ServerDispatcher SERVER_DISPATCHER = new ServerDispatcher();
    protected AbstractCollection<Connection> activeConnections
            = new ConcurrentLinkedQueue<>();
    protected AtomicBoolean isRunning;
    protected Thread mainThread;
    protected int port;
    protected ExecutorService THREAD_POOL;
    protected ServerSocket serverSocket;

    protected ServerDispatcher() {
        isRunning = new AtomicBoolean(false);
    }

    public static ServerDispatcher getInstance() {
        return SERVER_DISPATCHER;
    }

    public boolean start(int port) {
        if (!isRunning.get()) {
            this.port = port;
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException ex) {
                Logger.getLogger(ServerDispatcher.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            THREAD_POOL = Executors.newCachedThreadPool();
            mainThread = new Thread(this);
            isRunning.set(true);
            mainThread.start();
            return true;
        }

        return false;
    }

    public boolean stop() {
        if (isRunning.get()) {
            isRunning.set(false);
            for (Connection i : activeConnections) {
                i.closeConnection();
            }
            activeConnections.clear();
            try {
                serverSocket.close();
                THREAD_POOL.shutdownNow();
                mainThread.join();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(ServerDispatcher.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        }

        return false;
    }

    protected void listenForConnections(ServerSocket serverSocket) {
        while (isRunning.get()) {
            try {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setTcpNoDelay(MasterServerConstants.TCP_NO_DELAY);
                System.out.println(clientSocket.getRemoteSocketAddress());
                addConnection(new RegularConnection(clientSocket));
            } catch (IOException ex) {
                Logger.getLogger(ServerDispatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected void startConnectionCleaner() {
        Timer connectionCleaner = new Timer();

        /* This task checks the activeConnection list each PACKET_DELAY * 2 
          milliseconds and removes every inactive connection. */
        TimerTask removeInactiveConnections = new TimerTask() {
            @Override
            public void run() {
                if (!isRunning.get()) {
                    connectionCleaner.cancel();
                    return;
                }

                Iterator<Connection> it = activeConnections.iterator();
                while (it.hasNext()) {
                    Connection connection = it.next();
                    if (!connection.isActive()) {
                        System.out.println("removing");
                        ConsoleFrame.sendMessage(TimerTask.class.getSimpleName(),
                                "Removing connection with " + connection.getClientSocket().getInetAddress());
                        it.remove();
                    }
                }
            }
        };
        connectionCleaner.scheduleAtFixedRate(removeInactiveConnections, 0,
                MasterServerConstants.PACKET_DELAY * 2);
    }

    @Override
    public void run() {
        startConnectionCleaner();
        listenForConnections(serverSocket);
    }

    public List<Match> getActiveMatches() throws IOException {
        List<Match> activeMatches = new LinkedList<>();

        MatchConnection matchConnection;
        for (Connection connection : activeConnections) {
            if (connection.isActive() && connection instanceof MatchConnection) {
                matchConnection = (MatchConnection) connection;
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(),
                        "Sent match: " + matchConnection.getActiveMatch().getTitle()
                        + " to " + connection.getClientSocket().getInetAddress());
                System.out.println("Sent match: " + matchConnection.getActiveMatch().getTitle());
                activeMatches.add(matchConnection.getActiveMatch());
            }
        }

        return activeMatches;
    }

    public List<String> getLocalConnections() {
        List<String> connections = new LinkedList<>();

        for (Connection connection : activeConnections) {
            if (connection.isActive()) {
                connections.add(connection.getClientSocket().getInetAddress().getHostAddress());
            }
        }
        return connections;
    }

    public void addConnection(Connection connection) {
        THREAD_POOL.execute(connection);
        activeConnections.add(connection);
        ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Added connection with " + connection.getClientSocket().getInetAddress());
    }
}
