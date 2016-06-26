package Engine;

import Console.ConsoleFrame;
import java.awt.geom.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Game Entity class provides a framework for Bullet,Cannon and Tank classes the
 * methods implemented in this class should work for normal 'X'-'Y' Cartesian
 * coordinate system so they would probably need to be altered to work with
 * origin in the top left
 */
public abstract class GameEntity implements TransformInterface, Serializable {

    final static protected ArrayList<GameEntity> ENTITY_LIST = new ArrayList<>();
    static protected int currentIndex = 0;

    protected int id;
    //use Area so we can transform using affinetransformer
    //transformation applied to area
    private final AffineTransform transformation;
    protected double x, y;//this is top left corner of the rectangle
    protected double angle, speed, damage, width, height;

    /**
     * Constructs an entity at position 'xPos' 'yPos' with speed of 'spd',
     * damage of 'dmg' and rotation of 'ang'.
     *
     * @param id
     * @param xPos a double value representing position on the 'X' axis
     * @param yPos a double value representing position on the 'Y' axis
     */
    public GameEntity(int id, double xPos, double yPos) {
        this.id = id;
        x = xPos;
        y = yPos;
        transformation = new AffineTransform();
        transformation.setToIdentity();

    }

    /**
     * Gets the speed of the entity.
     *
     * @return a double value representing the speed of the entity
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the speed of then entity at 'spd' if 'spd'>0.
     *
     * @param spd a double value representing the desired speed for the entity
     */
    public void setSpeed(double spd) {
        speed = (spd > 0) ? spd : speed;
    }

    /**
     * Gets the damage of the entity.
     *
     * @return a double value representing the damage of the entity
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Sets the damage of then entity at 'dmg' if 'dmg' >0.
     *
     * @param dmg a double value representing the desired damage for the entity
     */
    public void setDamage(double dmg) {
        damage = (dmg > 0) ? dmg : damage;
    }

    /**
     * Gets the angle of the current rotation.
     *
     * @return a double value representing the angle of the current rotation
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Sets the angle of the current rotation at 'ang' % 360.
     *
     * @param ang a double value representing the desired angle of the entity
     */
    public void setAngle(double ang) {
        angle = ang % 360;
    }

    /**
     * Gets the area of the current entity
     *
     * @return a Area object representing the area of the current entity
     */

    public Area getArea() {
        transformation.setToIdentity();
        Area area = new Area(new Rectangle2D.Double(x, y, width, height));
        transformation.rotate(angle);
        area.transform(transformation);
        return area;
    }

    /**
     * Checks for collision .
     *
     * @param obj representing the object that we check collision for
     * @return if the objects collide then the intersection object it returned
     * otherwise null is returned
     */
    public boolean collision(GameEntity obj) {
        return getArea().intersects(obj.getRectangle());
    }

    /**
     * Get the x coordinate.
     *
     * @return a double value.
     */
    public double getX() {
        return x;
    }

    /**
     * Get the y coordinate.
     *
     * @return a double value.
     */
    public double getY() {
        return y;
    }

    /**
     * Set the x coordinate.
     *
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Set the y coordinate.
     *
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public void rotate(double degrees) {
        angle = (angle + degrees) % 360;
    }

    /**
     * Gets the rectangle of the current entity
     *
     * @return a Rectangle2D.Double value
     */
    public Rectangle2D.Double getRectangle() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    /**
     * Creates a deep clone using serialization.
     *
     * @return A deep clone of the current object.
     */
    public GameEntity deepClone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(this);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);

            return (GameEntity) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            ConsoleFrame.sendMessage("GameEntity: ", ex.getMessage());
        }

        return null;
    }

    @Override
    public String toString() {
        return "GameEntity{" + "id=" + id + ", transformation=" + transformation + ", x=" + x + ", y=" + y + ", angle=" + angle + ", speed=" + speed + ", damage=" + damage + '}';
    }

}
