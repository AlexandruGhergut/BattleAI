package Editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author Alexandra-Pc
 */
public class EditorMenuListener implements MouseListener{

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Component menuItem = e.getComponent();
        menuItem.setBackground(Color.WHITE);
        menuItem.setForeground(Color.BLACK);
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        e.getComponent().setBackground(Color.BLACK);
    }
    
}
