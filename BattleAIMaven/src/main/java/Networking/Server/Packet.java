package Networking.Server;

import Engine.GameEntity;
import java.io.Serializable;
import java.util.ArrayList;

public class Packet implements Serializable {

    private final ArrayList<ArrayList<GameEntity>> frames;
    private int index, added;
    static int capacity = 60;
    private boolean ready;
    
    public Packet() {
        frames = new ArrayList<>(capacity);
        ready = false;
    }

    /**
     * Adds a frame to the packet. WARNING: If the packet has reached maximum
     * capacity no more frames will be added.
     *
     * @param toAdd Frame to add the packet
     */
    public void addFrame(ArrayList<GameEntity> toAdd) {
        if (added == capacity) {
            return;
        }

        ArrayList<GameEntity> copy = new ArrayList<>(toAdd.size());

        for (int i = 0; i < toAdd.size(); i++) {
            copy.add(toAdd.get(i).deepClone());
        }

        frames.add(copy);
        added++;
        
        if(added == capacity){
            ready = true;
        }
    }
    
    /**
     * Marks the packet as ready, no matter how many frames it contains.
     * CAUTION: It is only meant to be used on the *last* packet.
     */
    
    public void setReady(){
        ready = true;
    }

    /**
     * Consumes a frame.
     *
     * @return The next frame the packet contains.
     */
    public ArrayList<GameEntity> consume() {
        if (index < frames.size()) {
            return frames.get(index++);
        }
        return null;
    }

    /**
     * To be used with consume()
     *
     * @return How many frames are left.
     */
    public int framesLeft() {
        return frames.size() - index;
    }

    /**
     *
     * @return true if the packet is ready to be sent
     */
    public boolean ready() {
        return ready;
    }
}
