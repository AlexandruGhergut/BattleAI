package Networking.Requests;

import java.io.ObjectOutputStream;

public class BooleanResponse extends Request {

    private final boolean success;

    public BooleanResponse(boolean success) {
        super(RequestType.BOOLEAN_RESPONSE);
        this.success = success;
    }

    public boolean getValue() {
        return success;
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {
    }

}
