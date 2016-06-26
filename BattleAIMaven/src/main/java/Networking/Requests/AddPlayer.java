package Networking.Requests;

import java.io.ObjectOutputStream;

public class AddPlayer extends Request {

    private final String username;

    public AddPlayer(String username) {
        super(RequestType.ADD_PLAYER);
        this.username = username;
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {
    }

    public String getUsername() {
        return username;
    }

}
