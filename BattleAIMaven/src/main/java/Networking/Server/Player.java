/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking.Server;

import java.io.Serializable;

/**
 * Singleton class representing the currently logged in player.
 */
public class Player implements Serializable {
    
    private static Player instance;
    private String username;
    
    private Player() {
    }
    
    public static Player getInstance() {
        if (instance == null)
            instance = new Player();
        return instance;
    }
    
    /**
     * Sets the player username.
     * @param username 
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gets the player username.
     * @return 
     */
    public String getUsername() {
        return username;
    }
    
    
}
