package Database;

import java.util.List;

/**
 * The InfoPlayer class acts as a layer that simplifies DatabaseHandler usage in
 * dealing with player information.
 */
public class InfoPlayer {

    private final static DatabaseHandler DB;

    static {
        DB = DatabaseHandler.getInstance();
    }

    /**
     * @param name Account username
     * @return true if this name is valid for LogIn, i.e this name doesn't exist
     * in Database, false otherwise
     */
    public static synchronized Boolean isValidName(String name) {
        return (DB.findName(name) != true);
    }

    /**
     * Returns true if user with username-name and password - pass exist,
     * otherwise returns false.
     *
     * @param name Account username
     * @param pass Account password
     * @return
     */
    public static synchronized Boolean isValidAccount(String name, String pass) {
        return DB.findAccount(name, pass);
    }

    /**
     * Push the player with User Name - name and Password - pass in database
     *
     * @param name
     * @param pass
     */
    public static synchronized void signUp(String name, String pass) {
        DB.pushPlayer(name, pass);
    }

    public static synchronized void removeAccount(String name, String pass) {
        DB.removePlayer(name, pass);
    }

    public static synchronized void setNumberOfPoints(String username, Integer numberOfPoints) {
        DB.setNoOfPoints(username, numberOfPoints);
    }

    public static synchronized void increasePoints(String username, int points) {
        DB.setNoOfPoints(username, getNumberOfPoints(username) + points);
    }

    public static synchronized void decreasePoints(String username, int points) {
        increasePoints(username, -points);

    }

    public static synchronized Integer getNumberOfPoints(String username) {
        return DB.getNoOfPoints(username);
    }

    /**
     * @param username Account username
     * @return List of matches in which Player participate
     */
    public static synchronized List getMatches(String username) {
        return DB.getMatches(username);
    }

    public static synchronized List getWonMatches(String username) {
        return DB.getWonMatches(username);
    }

    public static synchronized List getLostMatches(String username) {
        return DB.getLostMatches(username);
    }

    /**
     *
     * @param username Account username
     * @param pass Account new password
     */
    public static synchronized void changePassword(String username, String pass) {
        DB.changePassword(username, pass);
    }
}
