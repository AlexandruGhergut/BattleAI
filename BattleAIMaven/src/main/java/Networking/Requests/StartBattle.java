package Networking.Requests;

import Networking.Client.ConnectionHandler;
import Visual.VisualEngine;
import java.awt.Frame;
import java.io.ObjectOutputStream;

public class StartBattle extends Request {

    public StartBattle() {
        super(RequestType.START_BATTLE);
    }
    
    @Override
    public void execute(ObjectOutputStream outputStream) {
    }
    
}
