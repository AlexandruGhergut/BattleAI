package Networking.Requests;

import java.io.ObjectOutputStream;

public class StartBattle extends Request {

    public StartBattle() {
        super(RequestType.START_BATTLE);
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {
    }

}
