package Engine;

import Compiler.SourceCompiler;
import Console.ConsoleFrame;
import Constants.VisualConstants;
import Editor.Source;
import Intelligence.IntelligenceTemplate;
import Intelligence.Semaphore;
import Intelligence.TankThread;
import Main.GameModes;
import Networking.Client.ConnectionHandler;
import Networking.Requests.EntityUpdateRequest;
import Networking.Server.ClientServerDispatcher;
import Networking.Server.PacketManager;
import Visual.VisualEngine;
import java.util.ArrayList;
import java.util.List;

public class IntelligenceControlThread extends Thread{
    private static IntelligenceControlThread instance;
    private final BulletUpdater bulletUpdater;
    private final ArrayList<TankThread> tankThreads;
    private final ArrayList<Semaphore> semaphores;
    private boolean running;
    
//    public IntelligenceControlThread(ArrayList<Source> surse){
//        IntelligenceTemplate playerCode;
//        tankThreads = new ArrayList<>();
//        semaphores = new ArrayList<>();
//        
//        running = true;
//        
//        for(int i = 0; i<surse.size(); i++){
//            
//            synchronized(GameEntity.entityList){
//                new Tank(); //adds it to entityList
//            }
//            
//            playerCode = (IntelligenceTemplate)SourceCompiler.getInstanceOfSource(surse.get(i));
//            semaphores.add(new Semaphore());
//            tankThreads.add(new TankThread(playerCode, semaphores.get(i)));
//        }
//        
//        bulletUpdater = new BulletUpdater();
//    }
    
    //Testing
    //Couldn't figure out the compiler so I'm using this just for testing purposes
    //This constructor will be removed at a later date
    public IntelligenceControlThread(int numberOfTanks){
        GameEntity.entityList.clear();
        GameEntity.currentIndex = 0;
        
        IntelligenceTemplate playerCode;// = new IntelligenceTemplate();
        tankThreads = new ArrayList<>();
        semaphores = new ArrayList<>();
        
        running = true;
        
        for(int i = 0; i<numberOfTanks; i++){
            
            synchronized(GameEntity.entityList){
                new Tank(); //adds it to entityList
            }
            switch (i) {
                case 0:
                    playerCode = new Intelligence.TestTank1();
                    break;
                case 1:
                    playerCode = new Intelligence.TestTank2();
                    break;
                case 2:
                    playerCode = new Intelligence.TestTank3();
                    break;
                default:
                    playerCode = new IntelligenceTemplate();
                    break;
            }
            
            semaphores.add(new Semaphore());
            tankThreads.add(new TankThread(playerCode, semaphores.get(i)));
        }
        
        if(VisualEngine.getInstance().getMatchMode() == GameModes.SINGLEPLAYER ||
                VisualEngine.getInstance().getMatchMode() == GameModes.MULTIPLAYER_HOST)
            VisualEngine.getInstance().updateEntityList(GameEntity.entityList);
        
        bulletUpdater = new BulletUpdater();
        
        ConsoleFrame.sendMessage("IntelligenceControlThread","size = "+GameEntity.entityList.size());
        
    }
    //END testing
    
    //Testing
    //Couldn't figure out the compiler so I'm using this just for testing purposes
    //This constructor will be removed at a later date
    public IntelligenceControlThread(List<Source> surse){
        GameEntity.entityList.clear();
        GameEntity.currentIndex = 0;
        
        IntelligenceTemplate playerCode;// = new IntelligenceTemplate();
        tankThreads = new ArrayList<>();
        semaphores = new ArrayList<>();
        
        running = true;
        
        for(int i = 0; i<surse.size(); i++){
            
            synchronized(GameEntity.entityList){
                new Tank(); //adds it to entityList
            }
            playerCode = (IntelligenceTemplate) SourceCompiler.getInstanceOfSource(surse.get(i));
            
            semaphores.add(new Semaphore());
            tankThreads.add(new TankThread(playerCode, semaphores.get(i)));
        }
        
        if(VisualEngine.getInstance().getMatchMode() == GameModes.SINGLEPLAYER ||
                VisualEngine.getInstance().getMatchMode() == GameModes.MULTIPLAYER_HOST)
            VisualEngine.getInstance().updateEntityList(GameEntity.entityList);
        
        bulletUpdater = new BulletUpdater();
        
        ConsoleFrame.sendMessage("IntelligenceControlThread","size = "+GameEntity.entityList.size());
    }
    //END testing
    
    
    @Override
    public void run(){
        
        for(int i = 0; i<tankThreads.size(); i++) {
            tankThreads.get(i).start();
        }
        
        bulletUpdater.start();
        
        while(running) {
            
            synchronized (GameEntity.entityList) {
                //OLD WAY
                /*    for (int i = 0; i < tankThreads.size(); i++) {
                    if (semaphores.get(i).isGreen()) {
                        EntityUpdateRequest eur = new EntityUpdateRequest(GameEntity.entityList);
                        ClientServerDispatcher.getInstance().broadcastToAllExceptHost(eur);
                    }
                }
                 */
                
                    PacketManager.getInstance().addFrame(GameEntity.entityList);    //send the current frame
                
            }
            
            for(int i = 0; i < tankThreads.size(); i++){
                synchronized (semaphores.get(i)) {
                    if (semaphores.get(i).isGreen()) {
                        //to ensure that the enemy is always detected
                        ((Tank)GameEntity.entityList.get(i)).rotate(0.1);
                        ((Tank)GameEntity.entityList.get(i)).rotate(-0.1);
                        //end
                        semaphores.get(i).goRed();
                        semaphores.get(i).notify();
                    }
                }
            }
            
            try {
                this.sleep(1000/60);
            } catch (InterruptedException ex) {
                Console.ConsoleFrame.sendMessage("IntelligenceControlThread", ex.getMessage());
            }
        }
    }
    
    public void stopNicely(){
        running = false;
        
        bulletUpdater.stopNicely();
        for(int i = 0; i < tankThreads.size(); i++)
            tankThreads.get(i).stopNicely();
    }
}
