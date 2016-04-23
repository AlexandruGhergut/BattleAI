package Engine;

import java.io.Serializable;


>>>>>>> c9051eb5983ebf83e0479a6021d49412ee760d43
import Constants.EngineConstants;
import Constants.VisualConstants;
import Visual.VisualPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.*;

/**
 *
 * A bullet class used by tank class to destroy enemies.
 */

final public class Bullet extends GameEntity implements Serializable,TransformInterface, Drawable{

    Image bulletSprite ;
    
    public Bullet(int id, double xPos , double yPos ){
        super(id,xPos,yPos);                 
        //spd represents the speed of the bullet, dmg represents the damage that the bullet gives to an
        //enemy tank and ang represents the angle in wich the bullet is rotated
        //id represents the id of the tank the fired the bullet
        this.bulletSprite = VisualPanel.bulletSprite;
        width = VisualConstants.BULLET_WIDTH;
        height = VisualConstants.BULLET_HEIGHT;
        speed = EngineConstants.BULLET_SPEED;
        angle = EngineConstants.ANGLE;
        damage = EngineConstants.DAMAGE;
    }
    public void moveFront(){
        double s = Math.sin(angle * Math.PI/180.0);
        double c = Math.cos(angle * Math.PI/180.0);
        x += c*speed;
        y += s*speed;
    }
    /**
     * We get the id of the tank that fired the bullet .
     * @return a integer value 
     */
    public int getId(){
        return id;
    }
    
    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        AffineTransform at = g2.getTransform();
        
        g2.rotate(Math.toRadians(angle), x, y);
                                                       //8-9 FPS at ~1300 objects
        g2.setColor(Color.red);
        g2.fillRect((int)x, (int)y, (int)VisualConstants.BULLET_WIDTH, (int)VisualConstants.BULLET_HEIGHT);  
        
        g2.setTransform(at);
    }
}
