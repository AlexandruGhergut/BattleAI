/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking.Requests;

import Networking.Server.Match;
import java.io.ObjectOutputStream;

/**
 *
 * @author root
 */
public class HostMatch extends Request {

    private volatile Match match;
    
    public HostMatch(Match match) {
        super(RequestType.HOST_MATCH);
        this.match = match;
    }
    
    public Match getMatch() {
        return match;
    }

    @Override
    public void execute(ObjectOutputStream outputStream) {
    }
    
}
