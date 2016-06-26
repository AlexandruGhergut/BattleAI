package Networking.Requests;

import Networking.Server.ClientServerDispatcher;
import java.io.ObjectOutputStream;

public class ChatMessage extends Request {

    private final String message;

    public ChatMessage(String message) {
        super(RequestType.CHAT_MESSAGE);
        this.message = message;
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {
        ClientServerDispatcher.getInstance().broadcast(this);
    }

    public String getMessage() {
        return message;
    }

}
