/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking.Requests;

import Networking.Server.ClientServerDispatcher;
import java.io.ObjectOutputStream;

/**
 */
public class AddPlayer extends Request {

    private final String username;
    
    public AddPlayer(String username) {
        super(RequestType.ADD_PLAYER);
        this.username = username;
    }
    
    @Override
    public void execute(ObjectOutputStream outputStream) {
    }
    
    public String getUsername() {
        return username;
    }
    
}
