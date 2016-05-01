/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking.Requests;

import java.io.ObjectOutputStream;

/**
 *
 */
public class RegisterActivity extends Request {

    public RegisterActivity() {
        super(RequestType.REGISTER_ACTIVITY);
    }
    
    @Override
    public void execute(ObjectOutputStream outputStream) {
        
    }
    
}
