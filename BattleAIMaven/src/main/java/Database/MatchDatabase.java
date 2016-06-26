package Database;

import java.util.List;

/**
 *
 * @author Alexandra-Pc
 */
public class MatchDatabase {

    private final String winner;
    private final int noPlayers;
    private final Double duration;

    public MatchDatabase(String winner, int noPlayers, Double duration) {
        this.winner = winner;
        this.noPlayers = noPlayers;
        this.duration = duration;
    }

    public String getWinner() {
        return this.winner;
    }

    public int getNoPlayers() {
        return this.noPlayers;
    }

    public Double getDuration() {
        return this.duration;
    }

    public void pushMatch(List<String> playersList) {
        DatabaseHandler DB = DatabaseHandler.getInstance();

        int idMatch = DB.pushMatch(this);

        for (String player : playersList) {
            DB.pushAttend(idMatch, player);
        }
    }

}
