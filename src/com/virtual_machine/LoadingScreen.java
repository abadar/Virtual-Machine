package com.virtual_machine;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 *
 * @author ARSALAN
 */
public class LoadingScreen extends javax.swing.JFrame {

    /**
     * Creates new form Loading
     */
    public LoadingScreen() {
        initComponents();
        this.setPreferredSize(new Dimension(1030, 530));
        this.setMaximumSize(new Dimension(1030, 530));
        this.setMinimumSize(new Dimension(1030, 530));
        this.setBounds(150, 80, 1030, 530);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Pictures/icon.jpg")));

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("DINOLIX");
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        setResizable(false);
        getContentPane().setLayout(null);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Pictures/Dinolix.png"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 1040, 500);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}