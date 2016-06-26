package Engine;

import Console.ConsoleFrame;
import Constants.VisualConstants;
import java.util.ArrayList;

/**
 * Responsible with updating bullet positions and restoring tanks' energy over
 * time.
 *
 */
public class BulletUpdater extends Thread {

    private boolean running = true;
    ArrayList<GameEntity> entities = GameEntity.ENTITY_LIST;

    public boolean isOutOfTheArena(Bullet b) {
        return b.getX() < 0 || b.getX() > VisualConstants.ENGINE_WIDTH || b.getY() < 0 || b.getY() > VisualConstants.ENGINE_HEIGHT;
    }

    @Override
    public void run() {
        Bullet b;
        GameEntity entity;

        while (running) {
            synchronized (entities) {
                for (int i = 0; i < GameEntity.ENTITY_LIST.size(); i++) {
                    entity = GameEntity.ENTITY_LIST.get(i);
                    if (entity instanceof Bullet) {
                        b = (Bullet) entity;
                        if (isOutOfTheArena(b)) {
                            entities.remove(b);
                        } else {
                            b.moveFront();
                        }

                    }

                    //restoring the Tanks' energy over time
                    if (entity instanceof Tank) {
                        ((Tank) entity).restoreEnergy();
                    }
                }
            }

            try {
                Thread.sleep(1000 / VisualConstants.FRAME_RATE);
            } catch (InterruptedException ie) {
                ConsoleFrame.sendMessage("BulletUpdater", "Thread interrupted exception!");
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void stopNicely() {
        running = false;
    }
}
