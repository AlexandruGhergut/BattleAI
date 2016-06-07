package Database;

import Console.ConsoleFrame;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * DatabaseHandler is a singleton class, the instance of which handles the
 * database.
 */
public class DatabaseHandler {

    // JDBC driver name and database URL

    private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private final String DB_URL = "jdbc:mysql://localhost/?autoReconnect=true&useSSL=true";

    //  Database attributes
    private static String USER = "root";
    private static String PASS = "";
    private final String DB_NAME = "Test3"; 

    private Connection conn;
    
    private static DatabaseHandler instance;
    
    public static DatabaseHandler getInstance(String USER, String PASS) {
        if(instance == null){
            instance = new DatabaseHandler(USER,PASS);
        }
        return instance;
    } 
    
    public static DatabaseHandler getInstance() {
        if(instance == null){
            instance = new DatabaseHandler(USER, PASS);
        }
        return instance;
    }

    private DatabaseHandler(String USER, String PASS) {
        DatabaseHandler.USER = USER;
        DatabaseHandler.PASS = PASS;
        
        if (createDatabase() == true){
            createTables();
        }
    }

    /**
     * Register JDBC driver and open a connection
     */
    private synchronized void preliminaries() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException se) {
            // Handle errors for JDBC
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to find driver or some"
                    + " other sql error, please open sql(mysqld) " + se.getMessage());
        } catch (ClassNotFoundException e) {
            // Handle errors for Class.forName
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+e.getMessage());
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to initializa local JDBC");
        }
    }

    private synchronized void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException se) {
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+se.getMessage());
        }
    }

    /**
     * Create the database
     *
     * @return true if the database has been created, otherwise return false
     */
    private boolean createDatabase() {
        preliminaries();
        Statement stmt = null;

        try {
            stmt = conn.createStatement();

            String sql = "CREATE DATABASE " + DB_NAME;
            stmt.executeUpdate(sql);

        } catch (SQLException sqlException) {
            //if the database already exist
            if (sqlException.getErrorCode() == 1007) {
                closeConnection();
                return false;
            } else {
                closeConnection();
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+sqlException.getMessage());
                return false;
            }
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
                closeConnection();
                return false;
            }
        }
        closeConnection();
        return true;
    }

    private void createTables() {
        preliminaries();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();


            String sqlQuery = "USE "+ DB_NAME;
            stmt.executeUpdate(sqlQuery);

            //create PLAYER_DB
            sqlQuery = "CREATE TABLE PLAYER_DB "
                    + "(name VARCHAR(255) not NULL, "
                    + " password VARCHAR(255) not NULL, "
                    + " no_points INTEGER, "
                    + " PRIMARY KEY ( name ))ENGINE=InnoDB";
            stmt.executeUpdate(sqlQuery);

            // create MATCHES_DB
            sqlQuery = "CREATE TABLE MATCHES_DB "
                    + "(id_match INTEGER not NULL AUTO_INCREMENT, "
                    + " winner VARCHAR(255), "
                    + " no_players INTEGER not NULL, "
                    + " duration DOUBLE(10, 5) not NULL, "// ma omoara ea... ma omoara ea... eroareaaaa
                    + " PRIMARY KEY ( id_match )) ENGINE=InnoDB ";
            stmt.executeUpdate(sqlQuery);

            // create ATTEND
            sqlQuery = "CREATE TABLE ATTEND "
                    + " (id_match INTEGER not NULL REFERENCES "
                    + " MATCHES_BD(id_match) ON DELETE CASCADE ON UPDATE CASCADE, "
                    + " player_name VARCHAR(255) not NULL REFERENCES "
                    + " PLAYER_BD(name) ON DELETE CASCADE ON UPDATE CASCADE, "
                    + " PRIMARY KEY ( id_match, player_name ))ENGINE=InnoDB ";
            stmt.executeUpdate(sqlQuery);

        } catch (SQLException ex) {
            closeConnection();
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+ex.getMessage());
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                closeConnection();
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+se.getMessage());
            }
        }
    }

    /**
     * Returns true if name exist in Database on column name, otherwise returns
     * false
     * @param name
     * @return 
     */
    public synchronized Boolean findName(String name) {
        preliminaries();
        ResultSet result = null;
        PreparedStatement preparedStmt = null;

        try {
            String sqlQuery = "USE " + DB_NAME;
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.executeUpdate();

            sqlQuery = "SELECT name FROM PLAYER_DB"
                    + " WHERE name = ?";
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.setString(1, name);

            result = preparedStmt.executeQuery();

            return (result.next() == true);

        } catch (SQLException ex) {
            closeConnection();
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+ex.getMessage());
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (preparedStmt != null) 
                    preparedStmt.close();
                
            } catch (SQLException se) {
                closeConnection();
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+se.getMessage());
            }
        }
        return false;
    }

    /**
     * Returns true if name exist in Database on column name, and on the same
     * row, on column password is pass, otherwise returns false
     * @param name
     * @param pass
     * @return 
     */
    public synchronized Boolean findAccount(String name, String pass) {
        preliminaries();
        ResultSet result = null;
        PreparedStatement preparedStmt = null;

        try {
            String sqlQuery = "USE " + DB_NAME;
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.executeUpdate();

            sqlQuery = "SELECT name FROM PLAYER_DB "
                    + "WHERE name = ? and password = ?";
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.setString(1, name);
            preparedStmt.setString(2, pass);

            result = preparedStmt.executeQuery();

            return (result.next() == true);

        } catch (SQLException ex) {
            closeConnection();
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+ex.getMessage());
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (preparedStmt != null) 
                    preparedStmt.close();
                
            } catch (SQLException se) {
                closeConnection();
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+se.getMessage());
            }
        }
        return false;
    }

    public synchronized int getNoOfPoints(String name) {
        preliminaries();
        ResultSet result = null;
        PreparedStatement preparedStmt = null;

        try {
            String sqlQuery = "USE " + DB_NAME;
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.executeUpdate();

            sqlQuery = "SELECT no_points FROM PLAYER_DB "
                    + "WHERE name = ?";
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.setString(1, name);
            result = preparedStmt.executeQuery();

            if(result.next())
                return result.getInt(1);

        } catch (SQLException ex) {
            closeConnection();
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+ex.getMessage());
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (preparedStmt != null)
                    preparedStmt.close();
            } catch (SQLException se) {
                closeConnection();
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+se.getMessage());
            }
        }
        return 0;
    }

    public synchronized List<MatchDatabase> getMatches(String name) {
        List<MatchDatabase> matches = new ArrayList<>();
        preliminaries();
        PreparedStatement preparedStmt = null;
        ResultSet result = null;

        try {
            String sqlQuery = "USE " + DB_NAME;
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.executeUpdate();
            sqlQuery = "SELECT * FROM MATCHES_DB "
                    + "WHERE id_match IN (SELECT id_match FROM ATTEND "
                                        + "WHERE player_name = ?)";
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.setString(1, name);
            result = preparedStmt.executeQuery();

            while (result.next()) {
                //Retrieve by column name
                String winner = result.getString("winner");
                int noPlayers = result.getInt("no_players");
                Double duration = result.getDouble("duration");

                MatchDatabase match = new MatchDatabase(winner, noPlayers, duration);
                matches.add(match);
            }

        } catch (SQLException ex) {
            closeConnection();
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+ex.getMessage());
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (preparedStmt != null)
                    preparedStmt.close();
            } catch (SQLException se) {
                closeConnection();
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+se.getMessage());
            }
        }
        return matches;
    }

    public synchronized List<MatchDatabase> getWonMatches(String name) {
        List<MatchDatabase> matches = new ArrayList<>();
        preliminaries();
        PreparedStatement preparedStmt = null;
        ResultSet result = null;

        try {
            String sqlQuery = "USE " + DB_NAME;
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.executeUpdate();

            sqlQuery = "SELECT * FROM MATCHES_DB "
                    + "WHERE winner = ?";
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.setString(1, name);
            result = preparedStmt.executeQuery();

            while (result.next()) {
                //Retrieve by column name
                String winner = result.getString("winner");
                int noPlayers = result.getInt("no_players");
                Double duration = result.getDouble("duration");

                MatchDatabase match = new MatchDatabase(winner, noPlayers, duration);
                matches.add(match);
            }

        } catch (SQLException ex) {
            closeConnection();
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+ex.getMessage());
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (preparedStmt != null)
                    preparedStmt.close();
            } catch (SQLException se) {
                closeConnection();
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+se.getMessage());
            }
        }
        return matches;
    }

    public synchronized List<MatchDatabase> getLostMatches(String name) {
        List<MatchDatabase> matches = new ArrayList<>();
        preliminaries();
        PreparedStatement preparedStmt = null;
        ResultSet result = null;

        try {
            String sqlQuery = "USE " + DB_NAME;
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.executeUpdate();
            sqlQuery = "SELECT * FROM MATCHES_DB"
                    + " WHERE winner != ?" 
                    + " and id_match IN (SELECT id_match FROM ATTEND"
                                + " WHERE player_name = ?)";
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.setString(1, name);
            preparedStmt.setString(2, name);
            result = preparedStmt.executeQuery();

            while (result.next()) {
                //Retrieve by column name
                String winner = result.getString("winner");
                int noPlayers = result.getInt("no_players");
                Double duration = result.getDouble("duration");

                MatchDatabase match = new MatchDatabase(winner, noPlayers, duration);
                matches.add(match);
            }

        } catch (SQLException ex) {
            closeConnection();
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+ex.getMessage());
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (preparedStmt != null)
                    preparedStmt.close();
            } catch (SQLException se) {
                closeConnection();
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+se.getMessage());
            }
        }
        return matches;
    }

    public synchronized void pushPlayer(String name, String pass) {
        preliminaries();
        PreparedStatement preparedStmt = null;

        try {
            String sqlQuery = "USE " + DB_NAME;
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.executeUpdate();

            sqlQuery = "INSERT INTO PLAYER_DB "
                    + "VALUES (?, ? , 0)";
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.setString(1, name);
            preparedStmt.setString(2, pass);
            preparedStmt.executeUpdate();

        } catch (SQLException ex) {
            closeConnection();
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+ex.getMessage());
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (preparedStmt != null)
                    preparedStmt.close();
            } catch (SQLException se) {
                closeConnection();
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+se.getMessage());
            }
        }
    }

    public synchronized void setNoOfPoints(String userName, Integer numberOfPoints) {
        preliminaries();
        PreparedStatement preparedStmt = null;

        try {
            String sqlQuery = "USE " + DB_NAME;
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.executeUpdate();

            sqlQuery = "UPDATE PLAYER_DB"
                    + " SET no_points = ?"
                    + " WHERE name = ?";
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.setInt(1, numberOfPoints);
            preparedStmt.setString(2, userName);
            preparedStmt.executeUpdate();

        } catch (SQLException ex) {
            closeConnection();
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+ex.getMessage());
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (preparedStmt != null)
                    preparedStmt.close();
            } catch (SQLException se) {
                closeConnection();
            }
        }
    }

    public synchronized void changePassword(String userName, String pass) {
        preliminaries();
        PreparedStatement preparedStmt = null;

        try {
            String sqlQuery = "USE " + DB_NAME;
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.executeUpdate();

            sqlQuery = "UPDATE PLAYER_DB"
                    + " SET password = ?"
                    + " WHERE name = ?";
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.setString(1, pass);
            preparedStmt.setString(2, userName);
            preparedStmt.executeUpdate();

        } catch (SQLException ex) {
            closeConnection();
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+ex.getMessage());
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (preparedStmt != null)
                    preparedStmt.close();
            } catch (SQLException se) {
                closeConnection();
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+se.getMessage());
            }
        }
    }

    /**
     * 
     * @return the primary key of the match that has just been inserted into the table
     */
    public synchronized int pushMatch(MatchDatabase Match) {
        preliminaries();
        PreparedStatement preparedStmt = null;
        ResultSet result = null;

        try {
            String sqlQuery = "USE " + DB_NAME;
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.executeUpdate();

            sqlQuery = "INSERT INTO MATCHES_DB "
                    + "VALUES (NULL, ? , ?, ?)";
            preparedStmt = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStmt.setString(1, Match.getWinner());
            preparedStmt.setInt(2, Match.getNoPlayers());
            preparedStmt.setDouble(3, Match.getDuration());

            preparedStmt.executeUpdate();

            result = preparedStmt.getGeneratedKeys();

            if (result.next()) {
                return result.getInt(1);
            }

        } catch (SQLException ex) {
            closeConnection();
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+ex.getMessage());
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (preparedStmt != null)
                    preparedStmt.close();
            } catch (SQLException se) {
                closeConnection();
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+se.getMessage());
            }
        }
        return -1;
    }

    public synchronized void pushAttend(int idMatch, String player) {
        preliminaries();
        PreparedStatement preparedStmt = null;

        try {
            String sqlQuery = "USE " + DB_NAME;
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.executeUpdate();

            sqlQuery = "INSERT INTO ATTEND "
                    + "VALUES (?, ?)";
            preparedStmt = conn.prepareStatement(sqlQuery);
            preparedStmt.setInt(1, idMatch);
            preparedStmt.setString(2, player);
            preparedStmt.executeUpdate();


            closeConnection();

        } catch (SQLException ex) {
            closeConnection();
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+ex.getMessage());
        } finally {
            //finally block used to close resources
            try {
                if (preparedStmt != null)
                    preparedStmt.close();
            } catch (SQLException se) {
                closeConnection();
                ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed "+se.getMessage());
            }
        }
    }
    
    /**
     * Verifies if a connection to the database was successful.
     * @return true if connection was successful, false otherwise
     */
    public synchronized boolean testConnection() {
        preliminaries();
        
        Statement stmt = null;
        ResultSet result = null;
        boolean finalResult = false;
        
        try {
            String sqlQuery = "USE " + DB_NAME;
            stmt = conn.createStatement();
            stmt.executeUpdate(sqlQuery);
            
            sqlQuery = "SELECT 1 FROM DUAL";
            result = stmt.executeQuery(sqlQuery);
            if (result.next() && result.getInt(1) == 1)
                finalResult = true;
         } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection();
            
            try {
                if (result != null)
                    result.close();
                if (stmt != null)
                    stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return finalResult;
    }

}
