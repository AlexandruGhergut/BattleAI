package Networking.Requests;

import Engine.Tank;
import java.io.ObjectOutputStream;
import java.util.List;

public class EndBattle extends Request {
    private final List<Tank> tanks;
    
    public EndBattle(List<Tank> tanks) {
        super(RequestType.END_BATTLE);
        this.tanks = tanks;
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {
        
    }
    
    public List<Tank> getTankList() {
        return tanks;
    }
}
