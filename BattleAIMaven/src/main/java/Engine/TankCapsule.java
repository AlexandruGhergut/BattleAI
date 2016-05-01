package Engine;

import Engine.Tank;

public class TankCapsule {
    private Tank tank;
    
    public TankCapsule(){
        synchronized(GameEntity.entityList) {
            tank = (Tank)GameEntity.entityList.get(0);
            GameEntity.entityList.remove(0);
        }
    }
    
    public void moveForward(){
        tank.moveFront();
    }
}
