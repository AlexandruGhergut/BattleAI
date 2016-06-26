package Engine;

import java.awt.Graphics;
import java.io.Serializable;

/**
 *
 * @author Dragos-Alexandru
 */
public interface Drawable extends Serializable {

    public void draw(Graphics g);
}
