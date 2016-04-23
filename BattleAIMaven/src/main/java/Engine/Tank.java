package Engine;

import java.io.Serializable;

import Constants.EngineConstants;
import Constants.VisualConstants;
import Visual.VisualPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.*;

public class Tank extends GameEntity implements Serializable,MovementInterface, TransformInterface, Drawable {    
    protected Image tankSprite;
    protected String playerName;
    protected double life;
    protected Cannon cannon;
    private final int id;
    //the id of the tank will be the current number of instanced tank classes
    private static int staticId;
    /**
     * This block executes once when the class is loaded
     */
    static{
        staticId = 0;
    }
    
    public Tank(double xPos, double yPos,String playerName) {
        super(staticId,xPos, yPos);
        tankSprite  = VisualPanel.tankSprite;
        this.id = staticId++;
        this.life = 100;
        this.playerName = playerName;
        width = (int)VisualConstants.TANK_WIDTH;
        height = (int)VisualConstants.TANK_HEIGHT;
        cannon = new Cannon(staticId, xPos, yPos);
        damage = EngineConstants.DAMAGE;
        angle = EngineConstants.ANGLE;
        speed = EngineConstants.TANK_SPEED;
        life = EngineConstants.LIFE;
    }
    /**
     *  Gets the id of the tan
     * @return a integer value representing the id of the tank.
     */
    public int getId(){
        return id;
    }
    /**
     * Gets the life of the Tank.
     *
     * @return
     */
    public double getLife() {
        return life;
    }

    /**
     * Sets the life of the Tank at 'lfe', if the 'lfe' argument is not lower or
     * equal then 0.
     *
     * @param lfe a double value representing a Tanks life
     */
    public void setLife(double lfe) {
        if (lfe >= 0) {
            life = lfe;
        }
    }
    @Override
    public void rotate(double degrees){
        angle = (angle + degrees)%360;
        cannon.rotate(degrees);
    }
    /**
     *  Rotates the cannon of the tank by 'degrees' reported to the cannon's rotation angle
     * @param degrees a double value representing the rotation value
     */
    public void rotateCannon(double degrees){
        cannon.rotate(degrees);
    }
    
    @Override
    public void moveUp() {
        setY(getY()-1);
    }

    @Override
    public void moveDown() {
        setY(getY()+1);
    }

    @Override
    public void moveLeft() {
        setX(getX()-1);
    }

    @Override
    public void moveRight() {
        setX(getX()+1);
    }

    /**
     * Move the tank forward reported to it's current orientation angle.
     */
    public void moveFront(){
        double s = Math.sin(angle * Math.PI/180.0);
        double c = Math.cos(angle * Math.PI/180.0);
        x += c*speed;
        y += s*speed;
        //we store the angle of the cannon in cangle
        double cangle = cannon.getAngle();
        //set the cannon rotaton to the tank rotation  
        cannon.setAngle(angle);
        //then move the cannon front
        cannon.moveFront();
        //then we restore the cannon to it's former angle
        cannon.setAngle(cangle);
    }

    /**
     * Shoots a bullet.
     *
     * @return a Bullet object representing a bullet shoot by the tank.
     */
    public Bullet fire() {
        return cannon.fire();
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        AffineTransform at = g2.getTransform();
        
        g2.rotate(Math.toRadians(90), x+10, y+10);
        g2.rotate(Math.toRadians(angle), x+10, y+10);
        g2.drawImage(tankSprite, (int)x, (int)y, null);
        g2.setTransform(at); 
        
        cannon.draw(g);
        
        //draw health bar
        g2.setColor(Color.RED);
        g2.fillRect((int)x-2, (int)y-10,(int)VisualConstants.HEALTH_BAR_WIDTH, (int)VisualConstants.HEALTH_BAR_HEIGHT);
        g2.setColor(Color.decode("#0FB81A"));   //this is green
        g2.fillRect((int)x-2, (int)y-10, (int) (life/100*VisualConstants.HEALTH_BAR_WIDTH), (int)VisualConstants.HEALTH_BAR_HEIGHT);
        //end
        
        /*g2.rotate(Math.toRadians(90), cannon.getX()+10, cannon.getY()+10);
        g2.rotate(Math.toRadians(cannon.getAngle()), cannon.getX()+10, cannon.getY()+10);
        g2.drawImage(cannonSprite, (int)cannon.getX()+8, (int)cannon.getY(), null);*/
    }

    @Override
    public String toString() {
        return "Tank{" + " playerName=" + playerName + ", life=" + life + ", cannon=" +
                cannon + ", width=" + width + ", height=" + height + '}' + " " + super.toString();
    }

}
