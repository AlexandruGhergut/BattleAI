package Networking.Requests;

import Engine.Tank;
import java.io.ObjectOutputStream;
import java.util.List;

public class EndBattle extends Request {

    private final List<Tank> tanks;
    private final double duration; // expressed in seconds

    /**
     *
     * @param tanks
     * @param duration
     */
    public EndBattle(List<Tank> tanks, double duration) {
        super(RequestType.END_BATTLE);
        this.tanks = tanks;
        this.duration = duration;
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {

    }

    public List<Tank> getTankList() {
        return tanks;
    }

    public double getDuration() {
        return duration;
    }
}
