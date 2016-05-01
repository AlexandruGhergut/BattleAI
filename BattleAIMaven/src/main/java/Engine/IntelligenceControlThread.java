package Engine;

import Networking.Requests.EntityUpdateRequest;
import Client.ConnectionHandler;
import Compiler.SourceCompiler;
import Editor.Source;
import Engine.GameEntity;
import Intelligence.IntelligenceTemplate;
import Intelligence.Semaphore;
import Intelligence.TankThread;
import Networking.Requests.RequestType;
import java.io.IOException;
import java.util.ArrayList;

public class IntelligenceControlThread extends Thread{
    private static IntelligenceControlThread instance;
    private ArrayList<TankThread> tankThreads;
    private ArrayList<Semaphore> semaphores;
    private boolean running;
    
    public IntelligenceControlThread(ArrayList<Source> surse){
        IntelligenceTemplate playerCode;
        tankThreads = new ArrayList<>();
        semaphores = new ArrayList<>();
        
        running = true;
        
        for(int i = 0; i<surse.size(); i++){
            
            synchronized(GameEntity.entityList){
                new Tank();
            }
            
            playerCode = (IntelligenceTemplate)SourceCompiler.getInstanceOfSource(surse.get(i));
            semaphores.add(new Semaphore());
            tankThreads.add(new TankThread(playerCode, semaphores.get(i)));
        }
        
    }
    
    @Override
    public void run(){
        
        for(int i = 0; i<tankThreads.size(); i++) {
            tankThreads.get(i).start();
        }
        
        while(running) {  
            synchronized (GameEntity.entityList) {
                for (int i = 0; i < tankThreads.size(); i++) {
                    if (semaphores.get(i).isGreen()) {
                        EntityUpdateRequest eur = new EntityUpdateRequest(RequestType.ENTITIY_UPDATE, GameEntity.entityList);
                        try {
                            ConnectionHandler.getInstance().sendToMatch(eur);
                        } catch (IOException ex) {
                            Console.ConsoleFrame.sendMessage("IntelligenceControlThread", ex.getMessage());
                        }
                    }
                }
            }
            
            for(int i = 0; i < tankThreads.size(); i++){
                if(semaphores.get(i).isGreen()){
                    semaphores.get(i).goRed();
                    semaphores.get(i).notify();
                }
            }
            
            try {
                this.wait(1000/60);
            } catch (InterruptedException ex) {
                Console.ConsoleFrame.sendMessage("IntelligenceControlThread", ex.getMessage());
            }
        }
    }
    
    public void stopNicely(){
        running = false;
        
        for(int i = 0; i < tankThreads.size(); i++)
            tankThreads.get(i).stopNicely();
    }
}
