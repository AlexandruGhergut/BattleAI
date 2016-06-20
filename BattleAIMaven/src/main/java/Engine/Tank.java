package Engine;

import java.io.Serializable;

import Constants.EngineConstants;
import Constants.VisualConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.*;

public class Tank extends GameEntity implements Serializable,MovementInterface, TransformInterface, Drawable {
    protected double life;
    private double energy = 100;
    protected Cannon cannon;
    private final int tank_id;
    protected transient TankCapsule tankCapsule;
    private String name, author;
    private int rotate_state, move_state;
    private int score;
    
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
        this.tank_id = staticId++;
        this.life = 100;
        this.author = playerName;
        width = (int)VisualConstants.TANK_WIDTH;
        height = (int)VisualConstants.TANK_HEIGHT;
        cannon = new Cannon(staticId, xPos, yPos, this);
        damage = EngineConstants.DAMAGE;
        angle = EngineConstants.ANGLE;
        speed = EngineConstants.TANK_SPEED;
        life = EngineConstants.LIFE;
    }
    
    public Tank(String name, String author) {
        super(0,0,0);
        Rectangle tankRect =  new Rectangle();
        Rectangle otherTank = new Rectangle();
        
        this.name = name;
        this.author = author;
        
        x = (int)(Math.random()*1000%VisualConstants.ENGINE_WIDTH);
        y = (int)(Math.random()*1000%VisualConstants.ENGINE_HEIGHT);
        
        tankRect.x = (int) x;
        tankRect.y = (int) y;
        
        synchronized (this) {
            for (int i = 0; i < GameEntity.ENTITY_LIST.size(); i++) {
                if(GameEntity.ENTITY_LIST.get(i) instanceof Tank){
                Tank tank = (Tank) GameEntity.ENTITY_LIST.get(i);
                
                otherTank.x = (int)tank.getX();
                otherTank.y = (int)tank.getY();
                
                if(!isInsideArena() || tankRect.intersects(otherTank)){
                    x = (int) (Math.random() * 1000) % VisualConstants.ENGINE_WIDTH - 10 - VisualConstants.TANK_WIDTH;
                    y = (int) (Math.random() * 1000) % VisualConstants.ENGINE_HEIGHT - 10 - VisualConstants.TANK_HEIGHT;
                    i = 0;
                }
            }
            }
            
            //cannon = new Cannon(staticId, x, y);
        }
        
        
        //super(staticId,xPos, yPos);
        //this.x = xPos;
        //this.y = yPos;
        
        this.tank_id = staticId++;
        this.life = 100;
        width = (int)VisualConstants.TANK_WIDTH;
        height = (int)VisualConstants.TANK_HEIGHT;
        cannon = new Cannon(staticId, x, y, this);
        damage = EngineConstants.DAMAGE;
        angle = EngineConstants.ANGLE;
        speed = EngineConstants.TANK_SPEED;
        life = EngineConstants.LIFE;
        
    }
    
    public Point getCenter(){
       Point center = new Point((int)(this.x+VisualConstants.TANK_WIDTH/2),(int)(this.y+VisualConstants.TANK_HEIGHT/2));
       
       return center;
    }
    
    /**
     *  Gets the id of the tan
     * @return a integer value representing the id of the tank.
     */
    public int getId(){
        return tank_id;
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
        rotateCannon(degrees);
    }
    /**
     *  Rotates the cannon of the tank by 'degrees' reported to the cannon's rotation angle
     * @param degrees a double value representing the rotation value
     */
    public void rotateCannon(double degrees){
        if(rotate_state < Constants.EngineConstants.ROTATE_LIMIT){
            rotate_state++;
            cannon.rotate(degrees);
        }
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
    
    public void janitor(){
        resetStates();
        rotateCannon(0.1);
        rotateCannon(-0.1);
        rotate_state -=2;
    }
    
    private void resetStates(){
        rotate_state = move_state = 0;
    }
    
    /**
     * 
     * @param p - Point representing a tank.
     * @return Value which specifies whether the tank is inside the arena or not.
     */
    public boolean isInsideArena(Point p){
        return p.x-10 > 0 && p.y-10 > 0 && p.x+VisualConstants.TANK_WIDTH+10 < VisualConstants.ENGINE_WIDTH && p.y+VisualConstants.TANK_HEIGHT+40 < VisualConstants.ENGINE_HEIGHT;
    }
    
    /**
     * 
     * @return Value which specifies whether the tank is inside the arena or not.
     */
    public final boolean isInsideArena(){
        return this.x-10 > 0 && this.y-10 > 0 && this.x+VisualConstants.TANK_WIDTH+10 < VisualConstants.ENGINE_WIDTH && this.y + VisualConstants.TANK_HEIGHT+35 < VisualConstants.ENGINE_HEIGHT;
    }
    
    /**
     * Move the tank forward reported to it's current orientation angle.
     */
    public void moveFront(){
        if(move_state >= Constants.EngineConstants.MOVE_LIMIT)
            return;
        
        move_state++;
        double origX = x, origY = y;
        
        double s = Math.sin(angle * Math.PI / 180.0);
        double c = Math.cos(angle * Math.PI / 180.0);
        x += c * speed;
        y += s * speed;
        
        if (!isInsideArena()) {
            x = origX;
            y = origY;
            tankCapsule.hitArenaWall();
        } else {   
            //we store the angle of the cannon in cangle
            double cangle = cannon.getAngle();
            //set the cannon rotaton to the tank rotation  
            cannon.setAngle(angle);
            //then move the cannon front
            cannon.moveFront();
            //then we restore the cannon to it's former angle
            cannon.setAngle(cangle);
        }
        
    }

    /**
     * Shoots a bullet.
     *
     * @return a Bullet object representing a bullet shoot by the tank, or a null object if the tank is unable to fire.
     * 
     */
    public Bullet fire() {
        if(energy < 100)
            return null;
        
        energy = 0;
        return cannon.fire();
    }
    public double getEnergy(){
        return energy;
    }
    public void restoreEnergy(){
        if(energy < 100)
            energy += EngineConstants.ENERGY_RESTORE_RATE;
        
        if(energy > 100)
            energy = 100;
    }
    
    public void setTankCapsule(TankCapsule tankCapsule){
        this.tankCapsule = tankCapsule;
    }
    
    public void hitByBullet(){
        this.life -= 10;
        this.tankCapsule.gotHitByBullet();
    }
    
    public boolean inTheGame(){
        return life > 0;
    }
    
    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        AffineTransform at = g2.getTransform();
        
        g2.rotate(Math.toRadians(90), x+10, y+10);
        g2.rotate(Math.toRadians(angle), x+10, y+10);
        g2.drawImage(EngineConstants.TANK_SPRITE, (int)x, (int)y, null);
        g2.setTransform(at); 
        
        cannon.draw(g);
        
        //draw health bar
        g2.setColor(Color.RED);
        g2.fillRect((int)x-2, (int)y-10,(int)VisualConstants.HEALTH_BAR_WIDTH, (int)VisualConstants.HEALTH_BAR_HEIGHT);
        g2.setColor(Color.decode("#0FB81A"));   //this is green
        g2.fillRect((int)x-2, (int)y-10, (int) (life/100*VisualConstants.HEALTH_BAR_WIDTH), (int)VisualConstants.HEALTH_BAR_HEIGHT);
        //end
        
        //draw energy bar
        g2.setColor(Color.decode("#804000"));   //brownish
        g2.fillRect((int)x-2, (int)(y-10-VisualConstants.HEALTH_BAR_HEIGHT), (int)(VisualConstants.HEALTH_BAR_WIDTH), (int)VisualConstants.HEALTH_BAR_HEIGHT);
        g2.setColor(Color.decode("#ff9933"));   //orange
        g2.fillRect((int)x-2, (int)(y-10-VisualConstants.HEALTH_BAR_HEIGHT), (int)(energy/100*VisualConstants.HEALTH_BAR_WIDTH), (int)VisualConstants.HEALTH_BAR_HEIGHT);
        //end
        
        //draw name and author
        g2.setColor(Color.BLACK);
        Font f = g2.getFont();
        g2.setFont(g2.getFont().deriveFont(10f));
        g2.drawString(this.name, (int)(this.x+Constants.VisualConstants.TANK_WIDTH + 3), (int)(this.y+Constants.VisualConstants.TANK_HEIGHT/2-4));
        g2.drawString(this.author, (int)(this.x+Constants.VisualConstants.TANK_WIDTH + 3), (int)(this.y+Constants.VisualConstants.TANK_HEIGHT/2+4));
        g2.setFont(f);
        //end
    }
    
    public String getAuthor(){
        return this.author;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void addScore(int toAdd){
        this.score += toAdd;
    }
    
    public int getScore(){
        return score;
    }

    @Override
    public String toString() {
        return "Tank{" + " playerName=" + author + ", life=" + life + ", cannon=" +
                cannon + ", width=" + width + ", height=" + height + '}' + " " + super.toString();
    }

}
