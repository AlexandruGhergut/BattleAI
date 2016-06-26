package Constants;

import Console.ConsoleFrame;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author andrei
 */
public interface EngineConstants {

    public final double ANGLE = 0;
    public final double TANK_SPEED = 1.5;
    public final double CANNON_SPEED = 1.5;
    public final double BULLET_SPEED = 5;
    public final double DAMAGE = 5;
    public final double LIFE = 100;
    public final double ENERGY_RESTORE_RATE = 3;
    public final int MOVE_LIMIT = 2;
    public final int ROTATE_LIMIT = 5;

    public static Image TANK_SPRITE = getTankSprite();
    public static Image CANNON_SPRITE = getCannonSprite();
    public static Image BULLET_SPRITE = getBulletSprite();

    public static Image getTankSprite() {
        if (TANK_SPRITE != null) {
            return TANK_SPRITE;
        }

        Image image = null;
        try {
            image = ImageIO.read(new File(PathConstants.TANK_BODY_SPRITE_PATH));
        } catch (IOException ex) {
            ConsoleFrame.showError(ex.getMessage());
            System.exit(-1);
        }

        return image;
    }

    public static Image getCannonSprite() {
        if (CANNON_SPRITE != null) {
            return CANNON_SPRITE;
        }

        Image image = null;
        try {
            image = ImageIO.read(new File(PathConstants.TANK_CANNON_SPRITE_PATH));
        } catch (IOException ex) {
            ConsoleFrame.showError(ex.getMessage());
            System.exit(-1);
        }

        return image;
    }

    public static Image getBulletSprite() {
        if (BULLET_SPRITE != null) {
            return BULLET_SPRITE;
        }

        Image image = null;
        try {
            image = ImageIO.read(new File(PathConstants.BULLET_SPRITE_PATH));
        } catch (IOException ex) {
            ConsoleFrame.showError(ex.getMessage());
            System.exit(-1);
        }

        return image;
    }
}
