/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking.Requests;

import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author root
 */
public abstract class Request implements Serializable {
    private int type;
    
    public Request(int type) {
        this.type = type;
    }
    
    public int getType() {
        return type;
    }
    
    public abstract void execute(ObjectOutputStream outputStream);
}
