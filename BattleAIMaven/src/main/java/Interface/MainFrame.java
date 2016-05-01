package Interface;

import Constants.FrameConstants;
import Visual.VisualEngine;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Dragos-Alexandru
 */
public class MainFrame extends JFrame implements FrameConstants{
    
    private static MainFrame instance;
    public String localServerName = null;
    
    
    private boolean loggedIn = false;
    
    /**
     * Frame-ul principal al programului
     */
    private MainFrame(){
        super("BattleAI");
    }
    
    private void setup(){
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.add(new MainMenuPanel(this));
    }
    
    public static MainFrame getInstance(){
        if(instance == null){
            instance = new MainFrame();
            instance.setup();
            //need to tell the engine the window is closing);
            instance.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    if(VisualEngine.initialized()){
                        //telling the engine to close with the window
                        VisualEngine.getInstance().dispatchEvent(new WindowEvent(instance, WindowEvent.WINDOW_CLOSING));    //firing a closing event
                    }
                    super.windowClosing(e);
                }

            });
        }
        return instance;
    }
    
    public void changePanel(JPanel panel){
        this.getContentPane().removeAll();
        this.getContentPane().add(panel);
        this.getContentPane().revalidate();
        this.getContentPane().repaint();
    }
}
