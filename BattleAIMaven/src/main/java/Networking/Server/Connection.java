package Networking.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import java.io.ObjectOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Connection implements Runnable {
    
    protected Socket clientSocket;
    protected ObjectOutputStream outputStream;
    protected ObjectInputStream inputStream;
    protected Thread clientThread;
    protected volatile boolean threadRunning;
    protected AtomicInteger inactivityLevel;
    protected static final int MAX_INACTIVITY_LEVEL = 2;
    protected boolean activeConnection;
    
    /**
     * This constructor opens an output stream and an input stream on the 
     * provided socket.
     * @param clientSocket The client socket associated with the connection.
     * @param match The active match object to be used for the connection.
     */
    public Connection(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(clientSocket.getInputStream());
        activeConnection = true;
        inactivityLevel = new AtomicInteger(0);
    }
    
    /**
     * This constructor does not open new streams but makes use of existing ones.
     * @param clientSocket
     * @param inputStream
     * @param outputStream 
     */
    public Connection(Socket clientSocket, ObjectInputStream inputStream,
            ObjectOutputStream outputStream) {
        this.clientSocket = clientSocket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        activeConnection = true;
        inactivityLevel = new AtomicInteger(0);
    }
    
    /**
     * @return Returns the client socket associated with the connection.
     */
    public Socket getClientSocket() {
        return clientSocket;
    }
    
    /**
     * @return Returns an output stream associated with the current opened socket.
     */
    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }
    
    /**
     * @return Returns an input stream associated with the current opened socket.
     */
    public ObjectInputStream getInputStream() {
        return inputStream;
    }
    
    /**
     * @return Returns a boolean value indicating whether the connection
     * is active.
     */
    public boolean isActive() {
        return activeConnection;
    }
    
    @Override
    public void run() {
    }
}
