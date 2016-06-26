package Visual;

import Console.ConsoleFrame;
import Engine.Bullet;
import Engine.Tank;
import java.util.List;

/**
 * This class is temporary. It exists for testing purposes. It just makes all
 * the tanks fire at a random interval.
 *
 * @author Liviu
 */
@Deprecated
public class TankFiringThread extends Thread {

    final List<Tank> tanks;
    final List<Bullet> bullets;
    boolean running;

    public TankFiringThread(List<Tank> visualEntities, List<Bullet> bullets) {
        this.tanks = visualEntities;
        this.bullets = bullets;
        running = true;
    }

    @Override
    public void run() {
        Bullet auxBullet;

        while (running) {
            for (Tank tankAux : tanks) {
                auxBullet = tankAux.fire();

                synchronized (bullets) {
                    bullets.add(auxBullet);
                }
            }

            try {
                Thread.sleep(1000);    //random firing time (500 ms gets 20-30 bullets on the screen)
            } catch (InterruptedException ex) {
                ConsoleFrame.sendMessage("TankFiringThread", "Sleep interrupted");
            }

        }
    }

    public void stopFiring() {
        running = false;
    }
}
