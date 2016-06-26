package Networking.Requests;

import Networking.Server.Match;
import java.io.ObjectOutputStream;

public class HostMatch extends Request {

    private volatile Match match;

    public HostMatch(Match match) {
        super(RequestType.HOST_MATCH);
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {
    }

}
