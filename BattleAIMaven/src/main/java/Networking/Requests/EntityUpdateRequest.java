package Networking.Requests;

import Networking.Server.Packet;
import java.io.ObjectOutputStream;

public class EntityUpdateRequest extends Request {

    public Packet packet;

    public EntityUpdateRequest(Packet toSend) {
        super(RequestType.ENTITIY_UPDATE);
        packet = toSend;
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {

    }
}
