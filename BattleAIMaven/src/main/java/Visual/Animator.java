package Visual;

import Console.ConsoleFrame;
import Engine.Bullet;
import Engine.Tank;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * The separate thread which is responsible
 * with updating game entitites. It calls the 
 * repaint() function from JPanel.
 * 
 * @author Liviu
 * 
 */
public class Animator extends Thread{
    private final JPanel panel;
    private boolean running;
    private final int framerate = Constants.VisualConstants.FRAME_RATE;
    private final List<Tank> tanks;
    private final List<Bullet> bullets;
    private final TankFiringThread firingThread;
    private Thread paintThread;
    volatile boolean updateDone;
    long c = 0;
    
    
    public Animator(JPanel panel, List<Tank> tanks, List<Bullet> bullets){
        this.panel = panel;
        this.tanks = tanks;
        this.bullets = bullets;
        firingThread = new TankFiringThread(tanks,bullets);
        running = true;
    }
    
    @Override
    public void run(){
        ArrayList<Bullet> bulletsRemaining = new ArrayList<>();
        firingThread.start();
        double lel;
        
        while(running){
            //DO ENTITY UPDATES
            updateDone = false;
            for (Tank tankAux : tanks) {

                tankAux.rotateCannon((Math.random()*(Math.random()-0.5)*70)%360);
                tankAux.rotate((Math.random()*(Math.random()-0.5)*300)%360);
                
                lel = Math.random()*2;
                
                if (tankAux.getLife() - lel > 0) {
                    tankAux.setLife(tankAux.getLife() - lel);
                } else {
                    tankAux.setLife(100);
                }

                tankAux.moveFront();

            } 
            synchronized (bullets) {
                // List<Bullet> bulletsToBeRemoved = Collections.synchronizedList(new LinkedList<Bullet>());
                for (Bullet bulletAux : bullets) {
                    if (!(bulletAux.getX() > Constants.VisualConstants.ENGINE_WIDTH || bulletAux.getX() < 0
                            || bulletAux.getY() > Constants.VisualConstants.ENGINE_HEIGHT || bulletAux.getY() < 0)) {
                        //if the bullet is no longer on the screen, it's removed
                        //bulletsToBeRemoved.add(bulletAux);
                        bulletAux.moveFront();
                        bulletsRemaining.add(bulletAux);
                    } else {
                        // bulletAux.moveFront();
                    }
                }

                bullets.clear();

                for (int i = 0; i < bulletsRemaining.size(); i++) {
                    bullets.add(bulletsRemaining.get(i));
                }

                bulletsRemaining.clear();

                /*for(Bullet bulletAux:bulletsToBeRemoved){
                    bullets.remove(bulletAux);
                }*/
                updateDone = true;
            }
 
            
            if (paintThread == null || !paintThread.isAlive()) {
                paintThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (running) {
                            panel.repaint();

                            try {
                                Thread.sleep(1000 / framerate);
                            } catch (InterruptedException ex) {
                                ConsoleFrame.sendMessage("paintThread", "paintThread interrupted");
                            }
                        }

                    }
                });

                paintThread.start();

            }
            
            //panel.repaint();  
            
            try{
                Thread.sleep(1000/framerate);
            }
            catch(InterruptedException iex){
                ConsoleFrame.sendMessage("Animator", "Thread interrupted");
            }
        }
    }
    /**
     * Stops the animation in the game engine.
     * If you want to start the animation again, you'll 
     * have to create a new Animator object.
     */
    
    public void stopAnimation(){
        firingThread.stopFiring();
        running = false;
    }
}
