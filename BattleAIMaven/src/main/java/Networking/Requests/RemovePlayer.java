/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking.Requests;

import java.io.ObjectOutputStream;

/**
 *
 * @author root
 */
public class RemovePlayer extends Request {

    private String username;
    
    public RemovePlayer(String username) {
        super(RequestType.REMOVE_PLAYER);
        this.username = username;
    }
    
    @Override
    public void execute(ObjectOutputStream outputStream) {
    }
    
    public String getUsername() {
        return username;
    }
}
