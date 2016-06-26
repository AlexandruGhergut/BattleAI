package Networking.Requests;

import Database.InfoPlayer;
import Database.MatchDatabase;
import Engine.Tank;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EndBattleDBUpdate extends Request {

    private final List<Tank> tanks;
    private final double duration;

    public EndBattleDBUpdate(List<Tank> tanks, double duration) {
        super(RequestType.END_BATTLE_DB_UPDATE);
        this.tanks = tanks;
        this.duration = duration;
    }

    public EndBattleDBUpdate(EndBattle endBattleRequest) {
        super(RequestType.END_BATTLE_DB_UPDATE);
        this.tanks = endBattleRequest.getTankList();
        this.duration = endBattleRequest.getDuration();
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {
        List<String> players = new ArrayList<>();
        int winnerIndex = 0;

        for (int i = 0; i < tanks.size(); i++) {
            if (tanks.get(i).getScore() > tanks.get(winnerIndex).getScore()) {
                winnerIndex = i;
            }
            InfoPlayer.increasePoints(tanks.get(i).getAuthor(), tanks.get(i).getScore());
            players.add(tanks.get(i).getAuthor());
        }

        MatchDatabase matchDatabase
                = new MatchDatabase(tanks.get(winnerIndex).getAuthor(), players.size(),
                        duration);
        matchDatabase.pushMatch(players);
    }

}
