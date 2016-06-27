package Engine;

import Compiler.SourceCompiler;
import Console.ConsoleFrame;
import Source.Source;
import Intelligence.IntelligenceTemplate;
import Intelligence.Semaphore;
import Intelligence.TankThread;
import Enums.GameModes;
import Networking.Requests.EndBattle;
import Networking.Server.ClientServerDispatcher;
import Networking.Server.PacketManager;
import Visual.VisualEngine;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IntelligenceControlThread extends Thread {

    private final BulletUpdater bulletUpdater;
    private final ArrayList<TankThread> tankThreads;
    private final ArrayList<Semaphore> semaphores;
    private boolean running;
    private static int numberOfTanks;
    private static boolean pickedUpTheTrash = true;
    
    private final ArrayList<Tank> tanks = new ArrayList<>();
    private final List<Source> surse;

    private final ArrayList<Color> colors = new ArrayList<>();

    private final long startTime;

    public IntelligenceControlThread(List<Source> surse) {
        while(!pickedUpTheTrash){
            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
                ConsoleFrame.sendMessage("IntelligenceControlThread", "got interrupted while waiting for another ICT to finish ending");
            }
        }
        
        numberOfTanks = surse.size();
        GameEntity.currentIndex = 0;
        tanks.clear();
        this.surse = surse;

        setupColors();
        IntelligenceTemplate playerCode;// = new IntelligenceTemplate();
        tankThreads = new ArrayList<>();
        semaphores = new ArrayList<>();

        running = true;

        startTime = System.currentTimeMillis();
        for (int i = 0; i < surse.size(); i++) {

            synchronized (GameEntity.ENTITY_LIST) {
                Tank tank = new Tank(surse.get(i).getName(), surse.get(i).getAuthor()); //adds it to entityList
                tank.color = colors.get(i); //Setting the tank color
                GameEntity.ENTITY_LIST.add(tank);
                tanks.add(tank);
            }
            playerCode = (IntelligenceTemplate) SourceCompiler.getInstanceOfSource(surse.get(i));

            semaphores.add(new Semaphore());
            tankThreads.add(new TankThread(playerCode, semaphores.get(i)));
        }

        if (VisualEngine.getInstance().getMatchMode() == GameModes.SINGLEPLAYER
                || VisualEngine.getInstance().getMatchMode() == GameModes.MULTIPLAYER_HOST) {
            VisualEngine.getInstance().updateEntityList(GameEntity.ENTITY_LIST);
        }
 
        bulletUpdater = new BulletUpdater();

        ConsoleFrame.sendMessage("IntelligenceControlThread", "size = " + GameEntity.ENTITY_LIST.size());
    }
    //END testing

    @Override
    public void run() {
        int ingame;

        while(!BulletHitChecker.ready2Start()){
            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
                ConsoleFrame.sendMessage("IntelligenceControlThread", "Interrupted while waiting"
                        + " for BulletHitChecker to be ready to start again.");
            }
        }
        BulletHitChecker.getInstance().start();

        for (int i = 0; i < tankThreads.size(); i++) {
            tankThreads.get(i).start();
        }

        bulletUpdater.start();

        while (running) {
            ingame = 0;
            
            synchronized (GameEntity.ENTITY_LIST) {
                PacketManager.getInstance().addFrame(GameEntity.ENTITY_LIST);    //send the current frame     
            }

            for (int i = 0; i < tankThreads.size(); i++) {
                synchronized (semaphores.get(i)) {
                    if (semaphores.get(i).isGreen()) {
                        if (i < GameEntity.ENTITY_LIST.size() && ((Tank) GameEntity.ENTITY_LIST.get(i)).inTheGame()) {
                            //to ensure that the enemy is always detected
                            ((Tank) GameEntity.ENTITY_LIST.get(i)).janitor();
                            //end
                            semaphores.get(i).goRed();
                            semaphores.get(i).notify();
                        } else {
                            tankThreads.get(i).stopNicely();
                        }
                    }
                }
            }
            
            for(int i = 0; i < numberOfTanks; i++){
                synchronized(GameEntity.ENTITY_LIST){
                    if(((Tank)GameEntity.ENTITY_LIST.get(i)).inTheGame())
                        ingame++;
                }
            }

            if (ingame == 1) {
                this.gameOver();
            }

            try {
                IntelligenceControlThread.sleep(1000 / 60);
            } catch (InterruptedException ex) {
                Console.ConsoleFrame.sendMessage("IntelligenceControlThread", ex.getMessage());
            }
        }
    }

    public void gameOver() {
        this.stopNicely();
        long endTime = System.currentTimeMillis() - startTime;
        endTime /= 1000;
        PacketManager.getInstance().gameOver();
        VisualEngine.getInstance().closeWindow();
        ClientServerDispatcher.getInstance().broadcast(new EndBattle(tanks, endTime));
    }

    public static int getNumberOfTanks() {
        return numberOfTanks;
    }

    /**
     * To be called from a TankThread that wants to remove itself from the game.
     *
     * @param index
     */
    public void removeFromGame(int index) {
        synchronized (GameEntity.ENTITY_LIST) {
            ((Tank) GameEntity.ENTITY_LIST.get(index)).setLife(0);
        }
        System.out.print("Removed from game");
    }

    public void stopNicely() {
        pickedUpTheTrash = false;
        numberOfTanks = 0;
        
        PacketManager.getInstance().clearQueue();
        
        synchronized (GameEntity.ENTITY_LIST) {
            GameEntity.ENTITY_LIST.clear();
        }

        bulletUpdater.stopNicely();
        BulletHitChecker.getInstance().stopNicely();

        try {
            bulletUpdater.join();
            BulletHitChecker.getInstance().stopNicely();
            BulletHitChecker.getInstance().join();
        } catch (InterruptedException ex) {
            ConsoleFrame.sendMessage("IntelligeneControlThread", "InterruptedException during stopNicely()");
        }
        for (int i = 0; i < tankThreads.size(); i++) {
            try {
                tankThreads.get(i).stopNicely();
                tankThreads.get(i).join();
            } catch (InterruptedException ex) {
                ConsoleFrame.sendMessage("IntelligeneControlThread", "InterruptedException during stopNicely()");
            }
        }
        running = false;
        pickedUpTheTrash = true;
    }

    private void setupColors() {
        ArrayList<Color> colorsAux = new ArrayList<>();
        colorsAux.add(Color.red);
        colorsAux.add(Color.blue);
        colorsAux.add(Color.green);
        colorsAux.add(Color.yellow);
        colorsAux.add(Color.orange);
        colorsAux.add(Color.white);
        //colorsAux.add(Color.black);
        colorsAux.add(Color.pink);
        colorsAux.add(Color.cyan);
        colorsAux.add(Color.magenta);
        colorsAux.add(Color.lightGray);
        colorsAux.add(Color.darkGray);
        int n = colorsAux.size();
        for (int i = 0; i < n; i++) {
            Random rand = new Random();
            int j = rand.nextInt(colorsAux.size());
            colors.add(colorsAux.get(j));
            colorsAux.remove(j);
        }
    }

}
