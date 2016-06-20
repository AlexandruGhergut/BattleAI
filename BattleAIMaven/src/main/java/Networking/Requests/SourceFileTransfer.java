package Networking.Requests;

import Source.Source;
import Networking.Server.ClientServerDispatcher;
import java.io.ObjectOutputStream;

public class SourceFileTransfer extends Request {

    private String username;
    private final Source source;
    
    public SourceFileTransfer(String username, Source source) {
        super(RequestType.SOURCE_FILE_TRANSFER);
        this.source = source;
        this.username = username;
    }
    
    @Override
    public void execute(ObjectOutputStream outputStream) {
        ClientServerDispatcher.getInstance().broadcast(new SourceFileReceived(username, source.getName()));
    }
    
    public Source getSource() {
        return source;
    }
}
