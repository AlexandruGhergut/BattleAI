package Networking.Server;

import Engine.GameEntity;
import java.util.ArrayList;

public class PacketManager {
    private static PacketManager instance;
    private ArrayList<Packet> packetQueue;
    
    private PacketManager(){
        packetQueue = new ArrayList<>();
    }
    
    public static PacketManager getInstance(){
        if(instance == null){
            instance = new PacketManager();
        }
        
        return instance;
    }
    
    public void addFrame(ArrayList<GameEntity> frame){
        Packet available = null;
        
        //Searching for an available packet
        for(int i = 0; i < packetQueue.size(); i++){
            if(!packetQueue.get(i).ready()){
                available = packetQueue.get(i);
            }
        }
        //END
        
        if(available == null){
            available = new Packet();
            packetQueue.add(available);
        }
        
        available.addFrame(frame);
        
        sendReadyPackets(); //works asynchronously ofc
    }
    
    private void sendReadyPackets(){
        
        for(int i = 0; i < packetQueue.size() && i >= 0; i++){
            if(packetQueue.get(i).ready()){
                new PacketSenderThread(packetQueue.get(i)).start();
                packetQueue.remove(i--);
            }
        }
    }
}
