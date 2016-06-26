package Networking.Requests;

import Database.DatabaseHandler;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginAccount extends Request {

    private final String username;
    private final String password;

    public LoginAccount(String username, String password) {
        super(RequestType.LOGIN_ACCOUNT);
        this.username = username;
        this.password = password;
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {
        try {
            if (DatabaseHandler.getInstance().findAccount(username, password)) {
                outputStream.writeObject(new BooleanResponse(true));
            } else {
                outputStream.writeObject(new BooleanResponse(false));
            }
            outputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(LoginAccount.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
