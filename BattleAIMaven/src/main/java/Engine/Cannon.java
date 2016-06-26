package Engine;

import java.io.Serializable;
import Constants.EngineConstants;
import Constants.VisualConstants;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

/**
 * A Cannon class used by an instance of Tank class to launch instances of
 * Bullet class.
 */

final public class Cannon extends GameEntity implements Serializable, TransformInterface, Drawable {

    Tank parent;
    boolean detected;

    /**
     *
     * @param id
     * @param xPos
     * @param yPos
     * @param parent
     */
    public Cannon(int id, double xPos, double yPos, Tank parent) {
        //in the case of the cannon spd will represend the speed of moving on the board and dmg the damage that it
        super(id, xPos, yPos);
        //gives to the tank that it hit's and ang will represent the 
        //current angle of the bullet which will be the same
        width = VisualConstants.CANNON_WIDTH;
        height = VisualConstants.CANNON_HEIGHT;
        speed = EngineConstants.CANNON_SPEED;
        damage = EngineConstants.DAMAGE;
        angle = EngineConstants.ANGLE;
        this.parent = parent;
    }

    public void moveFront() {
        double s = Math.sin(angle * Math.PI / 180.0);
        double c = Math.cos(angle * Math.PI / 180.0);
        setX(getX() + c * speed);
        setY(getY() + s * speed);
    }

    public int det(Point v1, Point v2) {
        return v1.x * v2.y - v1.y * v2.x;
    }

    //http://mathworld.wolfram.com/TriangleInterior.html
    public boolean isInTriangle(Point a, Point b, Point c, Point toVerify) {
        double a1, b1;

        b.x -= a.x;
        b.y -= a.y;

        c.x -= a.x;
        c.y -= a.y;

        a1 = (double) (det(toVerify, c) - det(a, c)) / det(b, c);
        b1 = -(double) (det(toVerify, b) - det(a, b)) / det(b, c);

        return a1 > 0 && b1 > 0 && a1 + b1 < 1;
    }

    @Override
    public void rotate(double degrees) {
        angle = (angle + degrees) % 360;

        Tank t;
        GameEntity entity;
        Point me = new Point((int) this.x, (int) this.y);
        Point forward = getForwardPoint(me, angle, 1000);

        //searching for enemies
        if (detected) {
            detected = false;
            return;
        }
        synchronized (GameEntity.ENTITY_LIST) {
            for (int i = 0; i < GameEntity.ENTITY_LIST.size(); i++) {
                entity = GameEntity.ENTITY_LIST.get(i);

                if (entity instanceof Tank && ((Tank) entity).inTheGame() && entity != parent) {
                    t = (Tank) entity;
                    Point left, right, detectedTank;

                    left = getForwardPoint(new Point((int) x, (int) y), angle - VisualConstants.RADAR_SIZE / 2, 1000);
                    right = getForwardPoint(new Point((int) x, (int) y), angle + VisualConstants.RADAR_SIZE / 2, 1000);
                    //detectedTank = new Point((int)t.getX(), (int)t.getY());
                    detectedTank = t.getCenter();

                    if (isInTriangle(new Point((int) x, (int) y), left, right, detectedTank)) {
                        detected = true;
                        parent.tankCapsule.detectedEnemyTank(getDeltaAngle(parent.getCenter(), forward, detectedTank));
                    }
                }
            }
        }
        //end 
    }

    public static Point getForwardPoint(Point origin, double angle, double forwardDistance) {
        double s = Math.sin(Math.toRadians(angle)) * forwardDistance;
        double c = Math.cos(Math.toRadians(angle)) * forwardDistance;

        return new Point((int) (origin.x + c), (int) (origin.y + s));
    }

    /**
     *
     * @param a
     * @param b
     * @param c
     * @return Whether point C is above or below the line made by points A and B
     */
    public static int orientation(Point a, Point b, Point c) {
        double or = (c.y - a.y) * (b.x - a.x) - (b.y - a.y) * (c.x - a.x);

        if (or > 0) {
            return 1;
        }
        if (or < 0) {
            return -1;
        }

        return 0;
    }

    /**
     *
     * @param a
     * @param b
     * @param c
     * @return The angle in degrees between the cannon (points A and B) and
     * Point C
     */
    public double getDeltaAngle(Point a, Point b, Point c) {
        double numitor, numarator, angle;
        Point v = new Point(b.x - a.x, b.y - a.y),
                u = new Point(c.x - a.x, c.y - a.y);

        numarator = v.x * u.x + v.y * u.y;
        numitor = Math.sqrt(v.x * v.x + v.y * v.y) * Math.sqrt(u.x * u.x + u.y * u.y);

        angle = Math.toDegrees(Math.acos(numarator / numitor));

        if (((Double) angle).isNaN()) {
            return 0;
        }

        return orientation(a, b, c) * angle;
    }

    /**
     * Shoots a bullet.
     *
     * @return a Bullet object representing a bullet shoot by the tank.
     */
    public Bullet fire() {
        Point firePoint
                = getForwardPoint(
                        getForwardPoint(
                                getForwardPoint(
                                        new Point((int) x, (int) y),
                                        angle + 270,
                                        VisualConstants.CANNON_WIDTH / 2
                                ), 45, VisualConstants.TANK_WIDTH / 2 * Math.sqrt(2)
                        ),
                        angle,
                        VisualConstants.CANNON_HEIGHT
                );
        //firePoint = getForwardPoint(new Point((int)x, (int)y),angle,VisualConstants.CANNON_WIDTH);

        Bullet b = new Bullet(id, firePoint.x, firePoint.y, this.parent);
        b.setAngle(angle);
        b.setDamage(damage);
        b.setSpeed(EngineConstants.BULLET_SPEED);
        return b;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        //calculate dem origin point
        Point tankCenter = getForwardPoint(new Point((int) x, (int) y), 45, VisualConstants.TANK_WIDTH / 2 * Math.sqrt(2));
        Point cannonStart = getForwardPoint(tankCenter, 0, -VisualConstants.CANNON_WIDTH / 2);
        //end

        AffineTransform at = g2.getTransform();

        g2.rotate(Math.toRadians(angle + 270), tankCenter.x, tankCenter.y);

        //draw le reidahr
        //get reidahr far points
        Point r1 = getForwardPoint(cannonStart, VisualConstants.RADAR_SIZE / 2 + 90, 1000);
        Point r2 = getForwardPoint(cannonStart, -VisualConstants.RADAR_SIZE / 2 + 90, 1000);
        //end points

        Stroke s = g2.getStroke();

        g2.setStroke(new BasicStroke(0.5f));

        g2.setColor(parent.color);
        g2.drawLine(tankCenter.x, tankCenter.y, r1.x, r1.y);
        g2.drawLine(tankCenter.x, tankCenter.y, r2.x, r2.y);

        g2.setStroke(s);
        //end reidahr

        g2.drawImage(EngineConstants.CANNON_SPRITE, (int) cannonStart.x, (int) cannonStart.y, null);     //draw cannon

        g2.setTransform(at);
    }

}
