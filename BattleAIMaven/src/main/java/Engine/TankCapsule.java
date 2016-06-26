package Engine;

import java.io.Serializable;

/**
 * Encapsulation for the Tank class so that we can restrict the player's access
 * to it. However, the drawback is that the tank objects have to be separately
 * created in the entityList from the GameEntity interface. Instances of this
 * class will select a Tank object from the entityList in order.
 *
 */
public class TankCapsule implements Serializable {

    private Tank tank;
    private transient int index;

    public TankCapsule() {
        synchronized (GameEntity.ENTITY_LIST) {
            if (GameEntity.ENTITY_LIST.size() > 0) {
                if (GameEntity.currentIndex < GameEntity.ENTITY_LIST.size()) {
                    index = GameEntity.currentIndex;
                    tank = (Tank) GameEntity.ENTITY_LIST.get(GameEntity.currentIndex++);
                    tank.setTankCapsule(this);
                }
            }
        }
    }

    public void gotHitByBullet() {
    }

    public void hitArenaWall() {
    }

    public void hitEnemyTank() {
    }

    public void detectedEnemyTank(double enemyAngle) {
    }

    public final void moveFront() {
        tank.moveFront();
    }

    public final void rotate(double degrees) {
        tank.rotate(degrees);
    }

    public final void rotateCannon(double degrees) {
        tank.rotateCannon(degrees);
    }

    public final double getLife() {
        return tank.getLife();
    }

    public final double getEnergy() {
        return tank.getEnergy();
    }

    public final double getAngle() {
        return tank.getAngle();
    }

    public final void fire() {
        Bullet b = tank.fire();

        if (b != null) {
            synchronized (GameEntity.ENTITY_LIST) {
                GameEntity.ENTITY_LIST.add(b);
            }
        }
    }
}
