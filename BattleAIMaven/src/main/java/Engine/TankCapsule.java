package Engine;

import java.io.Serializable;

/**
 * Encapsulation for the Tank class so that we can restrict
 * the player's access to it. However, the drawback is that
 * the tank objects have to be separately created in the entityList
 * from the GameEntity interface. Instances of this class will select
 * a Tank object from the entityList in order.
 * 
 */
public class TankCapsule implements Serializable {
    private Tank tank;
    
    public TankCapsule(){
        synchronized(GameEntity.entityList) {
            if(GameEntity.entityList.size()>0){
                if(GameEntity.currentIndex < GameEntity.entityList.size()){
                    tank = (Tank)GameEntity.entityList.get(GameEntity.currentIndex++);
                    tank.setTankCapsule(this);
                }
            }
        }
    }
    
    public void gotHitByBullet() {} 
    public void hitArenaWall() {} 
    public void hitEnemyTank() {} 
    public void detectedEnemyTank(double enemyAngle) {} 
    
    public final void moveFront(){
        tank.moveFront();
    }
    
    public final void rotate(double degrees){
        tank.rotate(degrees);
    }
    
    public final void rotateCannon(double degrees){
        tank.rotateCannon(degrees);
    }
    
    public final void fire(){
            Bullet b = tank.fire();
            
            if (b != null) {
            synchronized (GameEntity.entityList) {
                GameEntity.entityList.add(b);
            }
        }
    }
}
