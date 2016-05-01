/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking.Requests;

import Networking.Server.ClientServerDispatcher;
import java.io.ObjectOutputStream;

/**
 *
 * @author root
 */
public class ChatMessage extends Request {

    private String message;

    public ChatMessage(String message) {
        super(RequestType.CHAT_MESSAGE);
        this.message = message;
    }
    
    @Override
    public void execute(ObjectOutputStream outputStream) {
        ClientServerDispatcher.getInstance().broadcast(this);
    }
    
    public String getMessage() {
        return message;
    }
    
}
