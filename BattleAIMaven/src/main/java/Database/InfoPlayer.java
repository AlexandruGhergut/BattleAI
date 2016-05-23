
package Database;

import java.util.List;

/**
 * The InfoPlayer class represents all the necessary information for an account 
 * of a player
 */
public class InfoPlayer {
    private String userName;
    private Integer numberOfPoints;
    private static DatabaseHandler DB = DatabaseHandler.getInstance();
    
    public InfoPlayer(String name){
        this.userName = name;
        this.numberOfPoints = 0;
    }
    
    public InfoPlayer(String name, Integer points){
        this.userName = name;
        this.numberOfPoints = points;
    }
    
    /**
     * Returns an object InfoPlayer with userName - name and his number of points    
     * @param name - the user name
     */
    public InfoPlayer singIn(String name){
        return new InfoPlayer(name, DB.getNoOfPoints(name));
    }
    
    /**
     * returns true if this name is valid for LogIn, i.e this name doesn't exist 
     *  in Database
     * @param name - the user name
     */
    public static Boolean isValidName (String name){
        return (DB.findName(name) != true);
    }
    
    /**
     * Returns true if user with username-name and password - pass exist, 
     * otherwise returns false.
     */
    public static Boolean isValidAccount (String name, String pass){
        return DB.findAccount(name, pass);
    }
    
     /**
     * Push the player with User Name - name and Password - pass in database
     * @param name
     * @param pass 
     * @return an object InfoPlayer with userName - name and his number of points
     */
    public static InfoPlayer logIn(String name, String pass){
        DB.pushPlayer(name, pass);
        return new InfoPlayer(name);
    }

    public void setNumberOfPoints(Integer numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
        DB.setNoOfPoints(this.userName, numberOfPoints);
    }

    public String getUserName() {
        return userName;
    }

    public Integer getNumberOfPoints() {
        return numberOfPoints;
    }
    
    /**
     * @param Player
     * @return List of matches in which Player participate
     */
    public List getMatches(){
        return DB.getMatches(userName);
    }
    
    public List getWonMatches(){
        return DB.getWonMatches(userName);
    }
    
    public List getLostMatches(){
        return DB.getLostMatches(userName);
    }
    
    /**
     * 
     * @param Player who want to change the password
     * @param pass new password
     */
    public void changePassword(String pass){
        DB.changePassword(userName, pass);
    }
}
