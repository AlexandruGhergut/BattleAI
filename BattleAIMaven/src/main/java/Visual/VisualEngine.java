package Visual;

import Console.ConsoleFrame;
import Constants.VisualConstants;
import Editor.Source;
import Engine.GameEntity;
import Engine.IntelligenceControlThread;
import Engine.Tank;
import java.awt.Dimension;
import java.util.ArrayList;

/**
 *
 * @author Liviu
 */
public class VisualEngine extends javax.swing.JFrame {

    /**
     * Creates new form VisualEngine
     */
    
    private int matchMode = VisualConstants.SINGLEPLAYER;
    IntelligenceControlThread ict;
    
    private static VisualEngine instance;
    
    private VisualEngine() {
        initComponents();
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
    }
    
    public static VisualEngine getInstance(){
        if(instance == null)
            instance = new VisualEngine();
        return instance;
    }
    
    public static boolean initialized(){
        return instance != null;
    }
    
    public void updateEntityList(ArrayList<GameEntity> newList){
        visualPanel1.entityList = newList;
    }
    
    public void setMatchMode(int matchMode){
        this.matchMode = matchMode;
    }
    
    public int getMatchMode(){
        return matchMode;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        visualPanel1 = new Visual.VisualPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Constants.VisualConstants.ENGINE_TITLE);
        setPreferredSize(new Dimension(Constants.VisualConstants.ENGINE_WIDTH, Constants.VisualConstants.ENGINE_HEIGHT));
        setResizable(false);
        setSize(new Dimension(Constants.VisualConstants.ENGINE_WIDTH, Constants.VisualConstants.ENGINE_HEIGHT));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        visualPanel1.setMaximumSize(new Dimension(Constants.VisualConstants.ENGINE_WIDTH, Constants.VisualConstants.ENGINE_HEIGHT));
        visualPanel1.setMinimumSize(new Dimension(Constants.VisualConstants.ENGINE_WIDTH, Constants.VisualConstants.ENGINE_HEIGHT));
        visualPanel1.setPreferredSize(new Dimension(Constants.VisualConstants.ENGINE_WIDTH, Constants.VisualConstants.ENGINE_HEIGHT));

        javax.swing.GroupLayout visualPanel1Layout = new javax.swing.GroupLayout(visualPanel1);
        visualPanel1.setLayout(visualPanel1Layout);
        visualPanel1Layout.setHorizontalGroup(
            visualPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        visualPanel1Layout.setVerticalGroup(
            visualPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(visualPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(visualPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
        if(matchMode == VisualConstants.SINGLEPLAYER){
            ict = new IntelligenceControlThread(3);
            ict.start();
        }
        
        visualPanel1.animator.start();   //starting the animator when the window is visible
        
        
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        visualPanel1.animator.stopAnimation();   //stopping the animator when the window is closing
        visualPanel1.bullets.clear();
        instance = null;    //the form's close operation is DISPOSE, so there's no point in keeping the old instance around
        
        ict.stopNicely();
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Visual.VisualPanel visualPanel1;
    // End of variables declaration//GEN-END:variables
}
