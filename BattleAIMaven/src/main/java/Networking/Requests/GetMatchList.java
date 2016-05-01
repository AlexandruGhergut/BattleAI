/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking.Requests;

import Networking.Server.Match;
import Networking.Server.ServerDispatcher;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class GetMatchList extends Request {

    public GetMatchList() {
        super(RequestType.GET_MATCH_LIST);
    }
    
    @Override
    public void execute(ObjectOutputStream outputStream) {
        try {
            List<Match> matches =
                    ServerDispatcher.getInstance().getActiveMatches();
            outputStream.reset();
            outputStream.writeObject(matches);
            outputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(GetMatchList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
