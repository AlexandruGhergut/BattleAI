/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import Engine.Tank;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author Dragos
 */
public class Scoreboard extends javax.swing.JFrame {

    private List<Tank> tanks;
    
    /**
     * Creates new form Scoreboard
     * @param tanks
     */
    public Scoreboard(List<Tank> tanks) {
        initComponents();
        this.tanks = tanks;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setAlwaysOnTop(true);
        this.setTitle("BattleAI - Scoreboard");
        
        Collections.sort(tanks, (Tank t, Tank t1) -> t1.getScore() - t.getScore());
        
        Object[][] data = new Object[8][4];
        
        for(int i = 0; i < 8; i++){
            if(i < tanks.size()){
                data[i][0] = i+1;
                data[i][1] = tanks.get(i).getAuthor();
                data[i][2] = tanks.get(i).getName();
                data[i][3] = tanks.get(i).getScore();
            }
        }
        
        playerTable.setModel(new javax.swing.table.DefaultTableModel(
            data,
            new String [] {
                "Place", "Player", "Source", "Points"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        playerTable.setEnabled(false);
        playerTable.getTableHeader().setReorderingAllowed(false);
        playerScrollPane.setViewportView(playerTable);
        if (playerTable.getColumnModel().getColumnCount() > 0) {
            playerTable.getColumnModel().getColumn(1).setResizable(false);
            playerTable.getColumnModel().getColumn(2).setResizable(false);
            playerTable.getColumnModel().getColumn(3).setResizable(false);
        }
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent we) {
                super.windowClosed(we);
                MainFrame.getInstance().setVisible(true);
            }
        });
        
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scoreboardLabel = new javax.swing.JLabel();
        playerScrollPane = new javax.swing.JScrollPane();
        playerTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        scoreboardLabel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        scoreboardLabel.setForeground(new java.awt.Color(255, 204, 0));
        scoreboardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        scoreboardLabel.setText("Scoreboard");

        playerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Place", "Player", "Source", "Points"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        playerTable.setEnabled(false);
        playerTable.getTableHeader().setReorderingAllowed(false);
        playerScrollPane.setViewportView(playerTable);
        if (playerTable.getColumnModel().getColumnCount() > 0) {
            playerTable.getColumnModel().getColumn(0).setResizable(false);
            playerTable.getColumnModel().getColumn(1).setResizable(false);
            playerTable.getColumnModel().getColumn(2).setResizable(false);
            playerTable.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(153, 153, 153)
                .addComponent(scoreboardLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(playerScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scoreboardLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playerScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane playerScrollPane;
    private javax.swing.JTable playerTable;
    private javax.swing.JLabel scoreboardLabel;
    // End of variables declaration//GEN-END:variables
}
