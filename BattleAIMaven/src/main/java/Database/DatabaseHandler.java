package Database;

import Console.ConsoleFrame;
import java.sql.*;

/**
 * DatabaseHandler is a singleton class, the instance of which handles the
 * database.
 */
public class DatabaseHandler {

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/";

    //  Database attributes
    private String USER = "root";
    private String PASS = "";
    private static final String DB_NAME = "Test1"; 

    Connection conn;
    
    private static DatabaseHandler instance;
    
    public static DatabaseHandler getInstance(String USER, String PASS) {
        if(instance == null){
            instance = new DatabaseHandler(USER,PASS);
        }
        return instance;
    }

    private DatabaseHandler(String USER, String PASS) {
        this.USER = USER;
        this.PASS = PASS;
        
        if (createDatabase() == true){
            createTables();
        }
    }

    /**
     * Register JDBC driver and open a connection
     */
    private void preliminaries() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException se) {
            // Handle errors for JDBC   
            se.printStackTrace();
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to find driver or some other sql error, please open sql(mysqld)");
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
            ConsoleFrame.sendMessage(this.getClass().getSimpleName(), "Failed to initializa local JDBC");
        }
    }

    private void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
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

        }  catch (SQLException sqlException) {
            //if the database already exist
            if (sqlException.getErrorCode() == 1007) {
                closeConnection();
                return false;
            } else {
                closeConnection();
                sqlException.printStackTrace();
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
            
            //create PLAYER_BD
            sqlQuery = "CREATE TABLE PLAYER_BD "
                    + "(name VARCHAR(255) not NULL, "
                    + " password VARCHAR(255) not NULL, "
                    + " nr_point INTEGER, "
                    + " PRIMARY KEY ( name ))";
            stmt.executeUpdate(sqlQuery);

            // create MATCHES_BD
            sqlQuery = "CREATE TABLE MATCHES_BD "
                    + "(id_match INTEGER not NULL, "
                    + " winner VARCHAR(255), "
                    + " nr_players INTEGER not NULL, "
                    + " durations DECIMAL(2,2) not NULL, "
                    + " robot_name VARCHAR(255) not NULL, "
                    + " PRIMARY KEY ( id_match )) ";
            stmt.executeUpdate(sqlQuery);

            // create ATTEND
            sqlQuery = "CREATE TABLE ATTEND "
                    + "(id_match INTEGER not NULL, "
                    + " player_name VARCHAR(255) not NULL, "
                    + " PRIMARY KEY ( id_match, player_name ), "
                    + " FOREIGN KEY (id_match) REFERENCES "
                    + DB_NAME + ".MATCHES_BD(id_match) "
                    + " ON DELETE CASCADE ON UPDATE CASCADE, "
                    + " FOREIGN KEY (player_name) REFERENCES "
                    + DB_NAME + ".PLAYER_BD(name) "
                    + " ON DELETE CASCADE ON UPDATE CASCADE) ";
            stmt.executeUpdate(sqlQuery);
            
        } catch (SQLException ex) {
            closeConnection();
            ex.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                closeConnection();
            }
        }
        closeConnection();
    }
}
