package Networking.Requests;

import java.io.ObjectOutputStream;

public class SourceFileRemoved extends Request {

    private final String username;

    public SourceFileRemoved(String username) {
        super(RequestType.SOURCE_FILE_REMOVED);
        this.username = username;
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {
    }

    public String getUsername() {
        return username;
    }
}
