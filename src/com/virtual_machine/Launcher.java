package com.virtual_machine;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
//import static virtual_machine.Virtual_Machine.listFilesForFolder;
//import static virtual_machine.Virtual_Machine.temp;

/**
 * 
 * @author arsalan
 */
public class Launcher extends javax.swing.JFrame {

    /**
     * Creates new form Launcher
     */
    DisplayCommandScreen dis = new DisplayCommandScreen();
    Memory mem = new Memory();
    Register Reg = new Register();
    PQueue queue = new PQueue(mem, Reg);
    Stack stack = new Stack(mem, Reg);
    Instruction Fun = new Instruction(Reg, mem, stack);
    InstructionDecoder instructionDecoder = new InstructionDecoder(Reg, mem, queue, stack, Fun);
    DashboardScreen m = new DashboardScreen(mem, Reg, queue, stack, instructionDecoder, dis);

    public Launcher() throws FileNotFoundException, IOException {
        initComponents();
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("")));
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Pictures/icon.jpg")));
        this.setPreferredSize(new Dimension(1030, 530));
        this.setMaximumSize(new Dimension(1030, 530));
        this.setMinimumSize(new Dimension(1030, 530));
        this.setBounds(150, 80, 1030, 530);
        String current = "";
        mem.Initialize(mem, Reg, queue, instructionDecoder, stack);
        File folder = new File("Hib");
        if (folder.exists()) {
            String temp;
            boolean m = false, r = false;
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isFile()) {
                    temp = fileEntry.getName();
                    if (temp.endsWith("Memory.txt") || temp.endsWith("Reg.txt")) {
                        if (temp.endsWith("Memory.txt")) {
                            m = true;
                        } else if (temp.endsWith("Reg.txt")) {
                            r = true;
                        }
                    }
                }
            }
            if (m && r) {
                Scanner inp1 = new Scanner(new File("Hib\\Memory.txt"));
                Scanner inp2 = new Scanner(new File("Hib\\Reg.txt"));
                int i = 0, i2 = 0;

                while (inp1.hasNextByte()) {
                    inp1.nextByte();// arr[i] = sc.nextByte();
                    i++;
                }
                inp1.close();
                while (inp2.hasNextByte()) {
                    inp2.nextByte();// arr[i] = sc.nextByte();
                    i2++;
                }
                inp2.close();
                if (i != 65536) {
                    m = false;
                    r = false;
                }
                if (i2 != 64) {
                    r = false;
                    m = false;
                }

                if (!m && !r) {
                    Delete_Folder();
                }

            }
            if (m && r) {
                Resume.setVisible(true);
                Power.setVisible(false);

                try {

                    Scanner memory = new Scanner(new File("Hib\\Memory.txt"));
                    int i = 0;
                    while (memory.hasNextByte()) {
                        mem.Set_Mem(i, memory.nextByte());// arr[i] = sc.nextByte();
                        i++;
                    }
                    System.out.print(i);
                    memory.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(DashboardScreen.class.getName()).log(Level.SEVERE, null, ex);
                }

                Scanner reg;
                try {
                    reg = new Scanner(new File("Hib\\Reg.txt"));
                    for (int i = 0; i < 16 && reg.hasNextShort(); i++) {
                        Reg.setSpecialPurposeRegister((short) mem.bytesToInt(reg.nextByte(), reg.nextByte()), i);
                    }
                    // register.setSpecialPurposeRegister(reg.nextShort(), i);
                    for (int i = 0; i < 16 && reg.hasNextShort(); i++) {
                        Reg.setGeneralPurposeRegister((short) mem.bytesToInt(reg.nextByte(), reg.nextByte()), i);
                    }
                    reg.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(DashboardScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
                        instructionDecoder.Print_Info();
                         queue.updatePID();
            } 

        } else {
            Power.setVisible(true);
            Resume.setVisible(false);
                    instructionDecoder.Print_Info();
                    queue.updatePID();

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        Power = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        Resume = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DINOLIX");
        setBackground(new java.awt.Color(0, 102, 102));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage(getIconImage());
        setResizable(false);
        getContentPane().setLayout(null);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(218, 0, 2, 510);

        jLabel1.setText(" Basic Information:");
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 70, 110, 14);

        jLabel2.setText("Total Memory:    64 KB");
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().add(jLabel2);
        jLabel2.setBounds(30, 130, 170, 20);

        jLabel3.setText("General Purpose: 16");
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().add(jLabel3);
        jLabel3.setBounds(30, 260, 160, 14);

        jLabel4.setText("Kernal Memory: 16 KB");
        jLabel4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().add(jLabel4);
        jLabel4.setBounds(30, 150, 170, 14);

        jLabel5.setText("User Memory:    48 KB");
        jLabel5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().add(jLabel5);
        jLabel5.setBounds(30, 170, 150, 14);

        jLabel6.setText("Registers:");
        jLabel6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().add(jLabel6);
        jLabel6.setBounds(10, 220, 110, 14);

        jLabel7.setText("Special Purpose:  16");
        jLabel7.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().add(jLabel7);
        jLabel7.setBounds(30, 240, 170, 14);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Pictures/main.png"))); // NOI18N
        jLabel8.setName(""); // NOI18N
        getContentPane().add(jLabel8);
        jLabel8.setBounds(410, 110, 450, 260);

        jLabel9.setText("Memory: ");
        jLabel9.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().add(jLabel9);
        jLabel9.setBounds(10, 110, 70, 14);

        Power.setText("Power On");
        Power.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Power.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PowerActionPerformed(evt);
            }
        });
        getContentPane().add(Power);
        Power.setBounds(40, 20, 120, 20);

        jLabel10.setText("Maximum Processes: 63");
        jLabel10.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().add(jLabel10);
        jLabel10.setBounds(10, 310, 160, 14);

        Resume.setText("Resume");
        Resume.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Resume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResumeActionPerformed(evt);
            }
        });
        getContentPane().add(Resume);
        Resume.setBounds(40, 20, 120, 20);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void PowerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PowerActionPerformed

        Runnable r = new Runnable() {
            public void run() {
                ShiftToLoad();
                Starting L = new Starting();
                L.setVisible(true);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
                }
                L.setVisible(false);
                m.setVisible(true);

            }
        };

        Thread thr1 = new Thread(r);
        this.setVisible(false);
        thr1.start();

//this.setVisible(false);
        //jPicture.setVisible(false);
        //jPicture.dispose();
        //this.setVisible(true);

    }//GEN-LAST:event_PowerActionPerformed

    private void ResumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResumeActionPerformed
        //Resume
//        this.setVisible(false);
//        m.setVisible(true);
        Delete_Folder();
        Runnable r = new Runnable() {
            public void run() {
                ShiftToLoad();
                LoadingScreen L = new LoadingScreen();
                L.setVisible(true);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
                }
                L.setVisible(false);
                m.setVisible(true);

            }
        };

        Thread thr1 = new Thread(r);
        this.setVisible(false);
        thr1.start();

    }//GEN-LAST:event_ResumeActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Launcher().setVisible(true);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Power;
    private javax.swing.JButton Resume;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables

    private void ShiftToLoad() {
        //        System.out.println("Current dir:"+current);
        //String currentDir = System.getProperty("user.dir");
        //   System.out.println("Current dir using System:" +currentDir);     
        jLabel1.setVisible(false);
        jLabel2.setVisible(false);
        jLabel3.setVisible(false);
        jLabel4.setVisible(false);
        jLabel5.setVisible(false);
        jLabel6.setVisible(false);
        jLabel7.setVisible(false);
        jLabel8.setVisible(false);
        jLabel9.setVisible(false);
        jLabel10.setVisible(false);
        Power.setVisible(false);
        jSeparator1.setVisible(false);
    }

    private void Delete_Folder() {
        File f = new File("Hib\\Memory.txt");
        f.delete();
        f = new File("Hib\\Reg.txt");
        f.delete();
        f = new File("Hib");
        f.delete();
    }
}
