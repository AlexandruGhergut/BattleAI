package Networking.Requests;

import java.io.ObjectOutputStream;

public class RegisterActivity extends Request {

    public RegisterActivity() {
        super(RequestType.REGISTER_ACTIVITY);
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {

    }

}
