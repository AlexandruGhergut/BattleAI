package Networking.Server;

import Networking.Requests.EntityUpdateRequest;

public class PacketSenderThread extends Thread {

    Packet toSend;

    public PacketSenderThread(Packet toSend) {
        this.toSend = toSend;
    }

    @Override
    public void run() {
        synchronized (toSend) {
            EntityUpdateRequest eur = new EntityUpdateRequest(toSend);
            ClientServerDispatcher.getInstance().broadcastToAllExceptHost(eur);
        }
    }
}
