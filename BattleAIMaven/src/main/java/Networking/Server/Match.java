package Networking.Server;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public class Match implements Serializable {

    private String title;
    private String IP;
    private int PORT;
    private final String OWNER;

    // The list of players in the match
    private final AbstractSet<String> players;

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

        players = new ConcurrentSkipListSet<>();
    }

    /**
     * @return Returns the title/name of the match.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the match.
     *
     * @param title Title of the match.
     */
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
     * Adds a player to the match.
     *
     * @param username Name of the player who will be added to the match.
     */
    public void addPlayer(String username) {
        players.add(username);
    }

    /**
     * Removes a player from the match.
     *
     * @param username Name of the player who will be removed from the match.
     */
    public void removePlayer(String username) {
        players.remove(username);
    }

    /**
     * @return Returns the maximum number of players the match can support.
     */
    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    /**
     * @return A list of players currently in the match.
     */
    public List<String> getPlayerList() {
        return new LinkedList<>(players);
    }

    /**
     * Sets a new maximum number of players.
     *
     * @param maxNumberOfPlayers The maximum number of players the match can
     * support. This should be higher than the current maximum.
     */
    public void setMaxNumberOfPlayers(int maxNumberOfPlayers) {
        if (maxNumberOfPlayers > this.maxNumberOfPlayers) {
            this.maxNumberOfPlayers = maxNumberOfPlayers;
        }
    }

    public String toListMatch() {
        return title + " / " + OWNER;
    }

    public void setIP(String ip) {
        this.IP = ip;
    }

    public void setPort(int port) {
        this.PORT = port;
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == null || !(ob instanceof Match)) {
            return false;
        }

        Match match = (Match) ob;

        return title.equals(match.title)
                && IP.equals(match.IP)
                && PORT == match.PORT
                && OWNER.equals(match.OWNER)
                && players.containsAll(match.players)
                && maxNumberOfPlayers == match.maxNumberOfPlayers;
    }
}
