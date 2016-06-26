package Networking.Requests;

import java.io.ObjectOutputStream;

public class SourceFileReceived extends Request {

    private final String username;
    private final String sourcename;

    public SourceFileReceived(String username, String sourcename) {
        super(RequestType.SOURCE_FILE_RECEIVED);
        this.username = username;
        this.sourcename = sourcename;
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {
    }

    public String getUsername() {
        return username;
    }

    public String getSourcename() {
        return sourcename;
    }
}
