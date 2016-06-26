package Networking.Requests;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class Request implements Serializable {

    private final int type;

    public Request(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public abstract void execute(ObjectOutputStream outputStream);
}
