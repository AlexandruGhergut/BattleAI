package Networking.Requests;

import Networking.Server.ClientServerDispatcher;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class GetPlayerStateList extends Request {

    public GetPlayerStateList() {
        super(RequestType.GET_PLAYER_STATE_LIST);
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {
        try {
            List<String> playerStates
                    = ClientServerDispatcher.getInstance().getPlayerStateList();
            outputStream.reset();
            outputStream.writeObject(playerStates);
            outputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(GetPlayerStateList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
