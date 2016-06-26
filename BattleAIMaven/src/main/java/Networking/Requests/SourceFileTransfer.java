package Networking.Requests;

import Source.Source;
import Networking.Server.ClientServerDispatcher;
import java.io.ObjectOutputStream;

public class SourceFileTransfer extends Request {

    private String username;
    private final Source source;
    private boolean removeSource;

    public SourceFileTransfer(String username, Source source) {
        super(RequestType.SOURCE_FILE_TRANSFER);
        this.source = source;
        this.username = username;
        removeSource = false;
    }

    public SourceFileTransfer(String username) {
        this(username, null);
        removeSource = true;
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {
        if (!removeSource) {
            ClientServerDispatcher.getInstance().broadcast(new SourceFileReceived(username, source.getName()));
        } else {
            ClientServerDispatcher.getInstance().broadcast(new SourceFileRemoved(username));
        }
    }

    public Source getSource() {
        return source;
    }

    public boolean isRemovingSource() {
        return removeSource;
    }
}
