package Engine;

import Compiler.SourceCompiler;
import Console.ConsoleFrame;
import Source.Source;
import Intelligence.IntelligenceTemplate;
import Intelligence.Semaphore;
import Intelligence.TankThread;
import Enums.GameModes;
import Interface.Scoreboard;
import Networking.Requests.EndBattle;
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
    private static int numberOfTanks;
    
    private ArrayList<Tank> tanks = new ArrayList<>();
    private List<Source> surse;
    
    public IntelligenceControlThread(List<Source> surse){
        numberOfTanks = surse.size();
        GameEntity.currentIndex = 0;
        
        tanks.clear();
        this.surse = surse;
        
        IntelligenceTemplate playerCode;// = new IntelligenceTemplate();
        tankThreads = new ArrayList<>();
        semaphores = new ArrayList<>();
        
        running = true;
        
        for(int i = 0; i<surse.size(); i++){
            
            synchronized(GameEntity.ENTITY_LIST){
                Tank tank = new Tank(surse.get(i).getName(), surse.get(i).getAuthor()); //adds it to entityList
                GameEntity.ENTITY_LIST.add(tank);
                tanks.add(tank);
            }
            playerCode = (IntelligenceTemplate) SourceCompiler.getInstanceOfSource(surse.get(i));
            
            semaphores.add(new Semaphore());
            tankThreads.add(new TankThread(playerCode, semaphores.get(i)));
        }
        
        if(VisualEngine.getInstance().getMatchMode() == GameModes.SINGLEPLAYER ||
                VisualEngine.getInstance().getMatchMode() == GameModes.MULTIPLAYER_HOST)
            VisualEngine.getInstance().updateEntityList(GameEntity.ENTITY_LIST);
        
        bulletUpdater = new BulletUpdater();
        
        ConsoleFrame.sendMessage("IntelligenceControlThread","size = "+GameEntity.ENTITY_LIST.size());
    }
    //END testing
    
    
    @Override
    public void run(){
        int ingame;
        
        BulletHitChecker.getInstance().start();
        
        for(int i = 0; i<tankThreads.size(); i++) {
            tankThreads.get(i).start();
        }
        
        bulletUpdater.start();
        
        while(running) {
            ingame = 0;
            
            synchronized (GameEntity.ENTITY_LIST) {
                PacketManager.getInstance().addFrame(GameEntity.ENTITY_LIST);    //send the current frame     
            }
        
            for(int i = 0; i < tankThreads.size(); i++){
                synchronized (semaphores.get(i)) {
                    if (semaphores.get(i).isGreen()) {
                        if (((Tank) GameEntity.ENTITY_LIST.get(i)).inTheGame()) {
                        //to ensure that the enemy is always detected
                        ((Tank)GameEntity.ENTITY_LIST.get(i)).janitor();
                        //end
                        ingame++;
                        semaphores.get(i).goRed();
                        semaphores.get(i).notify();
                    }
                        else{
                            tankThreads.get(i).stopNicely();
                        }
                    }
                }
            }
            
            if(ingame == 1){
                this.gameOver();
            }
            
            try {
                this.sleep(1000/60);
            } catch (InterruptedException ex) {
                Console.ConsoleFrame.sendMessage("IntelligenceControlThread", ex.getMessage());
            }
        }
    }
    
    public void gameOver(){
        this.stopNicely();
        VisualEngine.getInstance().closeWindow();
        ClientServerDispatcher.getInstance().broadcast(new EndBattle(tanks));
    }
    
    public static int getNumberOfTanks(){
        return numberOfTanks;
    }
    
    /**
     * To be called from a TankThread that wants to remove itself from the game.
     * @param t 
     */
    public void removeFromGame(int index){
        synchronized(GameEntity.ENTITY_LIST){
            ((Tank)GameEntity.ENTITY_LIST.get(index)).setLife(0);
        }
        System.out.print("Removed from game");
    }
    
    public void stopNicely(){
        synchronized(GameEntity.ENTITY_LIST){
            GameEntity.ENTITY_LIST.clear();
        }
        
        running = false;
        
        bulletUpdater.stopNicely();
        BulletHitChecker.getInstance().stopNicely();
        
        try {
            bulletUpdater.join();
            BulletHitChecker.getInstance().stopNicely();
            BulletHitChecker.getInstance().join();
        } catch (InterruptedException ex) { 
            ConsoleFrame.sendMessage("IntelligeneControlThread", "InterruptedException during stopNicely()");
        }
        for(int i = 0; i < tankThreads.size(); i++){
            try {
                tankThreads.get(i).stopNicely();
                tankThreads.get(i).join();
            } catch (InterruptedException ex) {
                ConsoleFrame.sendMessage("IntelligeneControlThread", "InterruptedException during stopNicely()");
            }
        }
    }
}
