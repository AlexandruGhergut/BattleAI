package Intelligence;

import Engine.TankCapsule;
import Engine.Inteligence;
import Engine.Tank; 

public class IntelligenceTemplate extends TankCapsule implements Inteligence{ 
    
    public boolean anaAreMere;
    
    public IntelligenceTemplate(){
        super();
    }
    
    public void run(){
        
        
    }

    @Override 
    public void gotHitByBullet() {} 

    @Override 
    public void hitArenaWall() {} 

    @Override 
    public void hitEnemyTank() {} 

    @Override 
    public void detectedEnemyTank(Tank enemy) {} 

}