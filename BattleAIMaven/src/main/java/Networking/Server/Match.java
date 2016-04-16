package Networking.Server;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class Match implements Serializable {
	
    private String title;
    private final String IP;
    private final int PORT;
    private final String OWNER;
    
    // The list of players in the match
    private AbstractCollection<String> players;
    
    private int maxNumberOfPlayers;
    
    /**
     * @param title The title/name of the match.
     * @param IP The IP address of the host.
     * @param PORT The port on which the match is hosted.
     * @param OWNER The name of the owner.
     * @param maxNumberOfPlayers The maximum number of players.
     */
    public Match(String title, String IP, int PORT, String OWNER,
            int maxNumberOfPlayers) {
        this.title = title;
        this.IP = IP;
        this.PORT = PORT;
        this.OWNER = OWNER;
        this.maxNumberOfPlayers = maxNumberOfPlayers;

        players = new TreeSet<String>();
        players.add(OWNER);
    }
    
    /**
     * @return Returns the title/name of the match.
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * @return Returns the IP address of the match host.
     */
    public String getIP() {
        return IP;
    }
    
    /**
     * @return Returns the port on which the match is hosted.
     */
    public int getPort() {
        return PORT;
    }
    
    /**
     * @return Returns a string containing the name of the owner.
     */
    public String getOwner() {
        return OWNER;
    }
    
    /**
     * @return Returns the number of players currently in the match.
     */
    public int getNumberOfPlayers() {
        return players.size();
    }
    
    /**
     * @return Returns the maximum number of players the match can support.
     */
    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }
    
    public List<String> getPlayerList(){
        return new LinkedList<>(players);
    }
    
    /**
     * Sets a new maximum number of players.
     * @param maxNumberOfPlayers The maximum number of players the match can
     * support. This should be higher than the current maximum.
     */
    public void setMaxNumberOfPlayers(int maxNumberOfPlayers) {
        if (maxNumberOfPlayers > this.maxNumberOfPlayers)
            this.maxNumberOfPlayers = maxNumberOfPlayers;
    }
    
    public String toListMatch(){
        return title+" / "+OWNER;
    }
}
