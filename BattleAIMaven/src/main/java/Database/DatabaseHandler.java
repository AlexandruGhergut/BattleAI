package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DatabaseHandler is a singleton class, the instance of which handles the
 * database.
 */
public class DatabaseHandler {

    // JDBC driver name and database URL
    private static final String JDBC_Driver = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/?autoReconnect=true&useSSL=true";

    //  Database attributes
    private static final String USER = "root";
    private static final String PASS = "Hai_sa_programam";
    private static final String dbName = "Test1";

    Connection conn;

    private static DatabaseHandler instance = new DatabaseHandler();

    public static DatabaseHandler getInstance() {
        return instance;
    }

    private DatabaseHandler() {
        if (createDatabase() == true) {
            createTables();
        }
    }

    /**
     * Register JDBC driver and open a connection
     */
    private void preliminaries() {
        try {
            Class.forName(JDBC_Driver);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException se) {
            // Handle errors for JDBC   
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
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

            String sql = "CREATE DATABASE " + dbName;
            stmt.executeUpdate(sql);

        } catch (SQLException sqlException) {
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

            String sqlQuery = "USE " + dbName;
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
            ex.printStackTrace();
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                closeConnection();
            }
        }
    }

    /**
     * Returns true if name exist in Database on column name, otherwise returns
     * false
     */
    public Boolean findName(String name) {
        preliminaries();
        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = conn.createStatement();

            String sqlQuery = "USE " + dbName;
            stmt.executeUpdate(sqlQuery);

            sqlQuery = "SELECT name FROM PLAYER_DB"
                    + " WHERE name = '" + name + "'";
            result = stmt.executeQuery(sqlQuery);

            return (result.next() == true);

        } catch (SQLException ex) {
            closeConnection();
            ex.printStackTrace();
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (stmt != null) {
                    conn.close();
                }
                result.close();
            } catch (SQLException se) {
                closeConnection();
            }
        }
        return false;
    }

    /**
     * Returns true if name exist in Database on column name, and on the same
     * row, on column password is pass, otherwise returns false
     */
    public Boolean findAccount(String name, String pass) {
        preliminaries();
        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = conn.createStatement();

            String sqlQuery = "USE " + dbName;
            stmt.executeUpdate(sqlQuery);

            sqlQuery = "SELECT name FROM PLAYER_DB "
                    + "WHERE name = '" + name + "' and password = '" + pass + "'";
            result = stmt.executeQuery(sqlQuery);

            return (result.next() == true);

        } catch (SQLException ex) {
            closeConnection();
            ex.printStackTrace();
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (stmt != null) {
                    conn.close();
                }
                result.close();
            } catch (SQLException se) {
                closeConnection();
            }
        }
        return false;
    }

    public int getNoOfPoints(String name) {
        preliminaries();
        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = conn.createStatement();

            String sqlQuery = "USE " + dbName;
            stmt.executeUpdate(sqlQuery);

            sqlQuery = "SELECT no_points FROM PLAYER_DB "
                    + "WHERE name = '" + name + "'";
            result = stmt.executeQuery(sqlQuery);

            return result.getInt("no_point");

        } catch (SQLException ex) {
            closeConnection();
            ex.printStackTrace();
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (stmt != null) {
                    conn.close();
                }
                result.close();
            } catch (SQLException se) {
                closeConnection();
            }
        }
        return 0;
    }

    public List<MatchDatabase> getMatches(String name) {
        List<MatchDatabase> matches = new ArrayList<MatchDatabase>();
        preliminaries();
        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = conn.createStatement();

            String sqlQuery = "USE " + dbName;
            stmt.executeUpdate(sqlQuery);

            sqlQuery = "SELECT * FROM MATCHES_DB "
                    + "WHERE id IN (SELECT id_match FROM ATTEND "
                    + "WHERE player_name = '" + name + "'"
                    + ")";
            result = stmt.executeQuery(sqlQuery);

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
            ex.printStackTrace();
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (stmt != null) {
                    conn.close();
                }
                result.close();
            } catch (SQLException se) {
                closeConnection();
            }
        }
        return matches;
    }

    public List<MatchDatabase> getWonMatches(String name) {
        List<MatchDatabase> matches = new ArrayList<MatchDatabase>();
        preliminaries();
        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = conn.createStatement();

            String sqlQuery = "USE " + dbName;
            stmt.executeUpdate(sqlQuery);

            sqlQuery = "SELECT * FROM MATCHES_DB "
                    + "WHERE winner = '" + name + "'";
            result = stmt.executeQuery(sqlQuery);

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
            ex.printStackTrace();
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (stmt != null) {
                    conn.close();
                }
                result.close();
            } catch (SQLException se) {
                closeConnection();
            }
        }
        return matches;
    }

    public List<MatchDatabase> getLostMatches(String name) {
        List<MatchDatabase> matches = new ArrayList<MatchDatabase>();
        preliminaries();
        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = conn.createStatement();

            String sqlQuery = "USE " + dbName;
            stmt.executeUpdate(sqlQuery);

            sqlQuery = "SELECT * FROM MATCHES_DB"
                    + " WHERE winner in not " + name
                    + " and id IN (SELECT id_match FROM ATTEND"
                    + " WHERE player_name = '" + name + "'"
                    + ")";
            result = stmt.executeQuery(sqlQuery);

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
            ex.printStackTrace();
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (stmt != null) {
                    conn.close();
                }
                result.close();
            } catch (SQLException se) {
                closeConnection();
            }
        }
        return matches;
    }

    public void pushPlayer(String name, String pass) {
        preliminaries();
        Statement stmt = null;

        try {
            stmt = conn.createStatement();

            String sql = "USE " + dbName;
            stmt.executeUpdate(sql);

            sql = "INSERT INTO PLAYER_DB "
                    + "VALUES ('" + name + "', '" + pass + "' , 0)";
            stmt.executeUpdate(sql);

        } catch (SQLException ex) {
            closeConnection();
            ex.printStackTrace();
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                closeConnection();
            }
        }
    }

    public void setNoOfPoints(String userName, Integer numberOfPoints) {
        preliminaries();
        Statement stmt = null;

        try {
            stmt = conn.createStatement();

            String sql = "USE " + dbName;
            stmt.executeUpdate(sql);

            sql = "UPDATE PLAYER_DB"
                    + " SET no_points = " + numberOfPoints
                    + " WHERE name = '" + userName + "'";
            stmt.executeUpdate(sql);

        } catch (SQLException ex) {
            closeConnection();
            ex.printStackTrace();
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                closeConnection();
            }
        }
    }

    public void changePassword(String userName, String pass) {
        preliminaries();
        Statement stmt = null;

        try {
            stmt = conn.createStatement();

            String sql = "USE " + dbName;
            stmt.executeUpdate(sql);

            sql = "UPDATE PLAYER_DB"
                    + " SET password = '" + pass + "'"
                    + " WHERE name = '" + userName + "'";
            stmt.executeUpdate(sql);

        } catch (SQLException ex) {
            closeConnection();
            ex.printStackTrace();
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                closeConnection();
            }
        }
    }

    public int pushMatch(MatchDatabase Match) {
        preliminaries();
        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = conn.createStatement();

            String sql = "USE " + dbName;
            stmt.executeUpdate(sql);

            sql = "INSERT INTO MATCHES_DB "
                    + "VALUES (NULL,'" + Match.getWinner() + "', "
                    + Match.getNoPlayers() + ", "
                    + Match.getDuration()
                    + ")";
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);// ma omoara ea ma omoara ea... eroareaaaa

            result = stmt.getGeneratedKeys();

            if (result.next()) {
                return result.getInt(1);
            }

        } catch (SQLException ex) {
            closeConnection();
            ex.printStackTrace();
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (stmt != null) {
                    conn.close();
                }
                result.close();
            } catch (SQLException se) {
                closeConnection();
            }
        }
        return -1;
    }

    public void pushAttend(int idMatch, String player) {
        preliminaries();
        Statement stmt = null;

        try {
            stmt = conn.createStatement();

            String sql = "USE " + dbName;
            stmt.executeUpdate(sql);

            sql = "INSERT INTO ATTEND "
                    + "VALUES (" + idMatch + ", '" + player + "')";
            stmt.executeUpdate(sql);

            closeConnection();

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
    }

    public void afisP() {
        preliminaries();
        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = conn.createStatement();

            String sqlQuery = "USE " + dbName;
            stmt.executeUpdate(sqlQuery);

            sqlQuery = "SELECT * FROM PLAYER_DB";
            result = stmt.executeQuery(sqlQuery);

            while (result.next()) {
                //Retrieve by column name
                String name = result.getString("name");
                String pass = result.getString("password");
                int noPoints = result.getInt("no_points");

                //Display values
                System.out.print("name: " + name);
                System.out.print(", pass: " + pass);
                System.out.println(", noPlayers: " + noPoints);
            }

        } catch (SQLException ex) {
            closeConnection();
            ex.printStackTrace();
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (stmt != null) {
                    conn.close();
                }
                result.close();
            } catch (SQLException se) {
                closeConnection();
            }
        }
    }

    public void afisM() {
        preliminaries();
        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = conn.createStatement();

            String sqlQuery = "USE " + dbName;
            stmt.executeUpdate(sqlQuery);

            sqlQuery = "SELECT * FROM MATCHES_DB ";
            result = stmt.executeQuery(sqlQuery);

            while (result.next()) {
                //Retrieve by column name
                int id = result.getInt("id_match");
                String winner = result.getString("winner");
                int noPlayers = result.getInt("no_players");
                Double duration = result.getDouble("duration");

                //Display values
                System.out.print("id: " + id);
                System.out.print(", winner: " + winner);
                System.out.print(", noPlayers: " + noPlayers);
                System.out.println(", duration: " + duration);
            }

        } catch (SQLException ex) {
            closeConnection();
            ex.printStackTrace();
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (stmt != null) {
                    conn.close();
                }
                result.close();
            } catch (SQLException se) {
                closeConnection();
            }
        }
    }
    
    public void afisA() {
        preliminaries();
        Statement stmt = null;
        ResultSet result = null;

        try {
            stmt = conn.createStatement();

            String sqlQuery = "USE " + dbName;
            stmt.executeUpdate(sqlQuery);

            sqlQuery = "SELECT * FROM ATTEND ";
            result = stmt.executeQuery(sqlQuery);

            while (result.next()) {
                //Retrieve by column name
                int id = result.getInt("id_match");
                String name = result.getString("player_name");

                //Display values
                System.out.print("id_match: " + id);
                System.out.println(", player: " + name);
            }

        } catch (SQLException ex) {
            closeConnection();
            ex.printStackTrace();
        } finally {
            //finally block used to close resources
            closeConnection();
            try {
                if (stmt != null) {
                    conn.close();
                }
                result.close();
            } catch (SQLException se) {
                closeConnection();
            }
        }
    }

}
