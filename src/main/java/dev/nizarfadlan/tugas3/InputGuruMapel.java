/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package dev.nizarfadlan.tugas3;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Iqbal Abdul Majid
 */
public class InputGuruMapel extends javax.swing.JFrame {

    /**
     * Creates new form inputGuruMapel
     */
    public InputGuruMapel() throws ClassNotFoundException, SQLException {
        initComponents();
        db.connection();
        bacaTabel();
        TampilCombo();
        this.setTitle("Form Guru Mapel - Program Penjadwalan SMP");
    }
     private void newRecord() {
        jComboBox1.setSelectedItem("pilih guru");
        jComboBox2.setSelectedItem("pilih mapel");
        idSelected = null;
    }
    
    private void bacaTabel() {
        try {
            stmt = db.openConnection();
            ResultSet res = stmt.executeQuery(
              "SELECT * FROM guru_mapel gm "
                 + "INNER JOIN guru g ON g.nidn = gm.guru "
                 + "INNER JOIN mapel m ON m.id = gm.mapel"
            );
            DefaultTableModel modelTable = (DefaultTableModel) jTable1.getModel();
            modelTable.setRowCount(0);
            while (res.next()) {
                String id = res.getString("id");
                String guru = res.getString("g.nama");
                String mapel = res.getString("m.nama");
                
                String[] dataListTemp = {id, guru, mapel};
                modelTable.addRow(dataListTemp);
            }

            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void editData() {
      try {
            prestt = db.conn.prepareStatement(
              "SELECT * FROM guru_mapel gm "
                 + "INNER JOIN guru g ON g.nidn = gm.guru "
                 + "INNER JOIN mapel m ON m.id = gm.mapel "
                 + "WHERE gm.id = ? LIMIT 1"
            );
            prestt.setString(1, idSelected);
            ResultSet res = prestt.executeQuery();
            while(res.next()) {
              jComboBox1.setSelectedItem(res.getString("guru") + " - " + res.getString("g.nama"));
              jComboBox2.setSelectedItem(res.getString("mapel") + " - " + res.getString("m.nama"));
            }
            prestt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private boolean deleteData() {
        try {
            prestt = db.conn.prepareStatement("DELETE FROM guru_mapel WHERE id = ?");
            prestt.setString(1, idSelected);
            int res = prestt.executeUpdate();
            prestt.close();
            
            return res == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
     
    }
    
    private void TampilCombo() {
    try {
        PreparedStatement statement = db.conn.prepareStatement("SELECT * FROM guru");
//        statement.setString(1, idSelected);
        ResultSet result = statement.executeQuery();
        
        jComboBox1.removeAllItems(); // Menghapus semua item di JComboBox sebelum mengisi ulang
        
       while (result.next()) {
            String id = result.getString("nidn");
            String guru = result.getString("nama");
            String guruText = id + " - " + guru;
            jComboBox1.addItem(guruText); // Menambahkan ID dan nama guru ke dalam JComboBox
        }
         statement = db.conn.prepareStatement("SELECT * FROM mapel");
//        statement.setString(1, idSelected);
        result = statement.executeQuery();
       while (result.next()) {
            String id = result.getString("id");
            String nama = result.getString("nama");
            String mapelText = id + " - " + nama;
            jComboBox2.addItem(mapelText); // Menambahkan ID dan nama mapel ke dalam JComboBox
        }
        statement.close();
        
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}

    private int checkMapelNumber(String nidn) {
      int count = 0;
      try {
        prestt = db.conn.prepareStatement("SELECT COUNT(guru) FROM guru_mapel WHERE guru = ?");
        prestt.setString(1, nidn);
        ResultSet res = prestt.executeQuery();
        
        if (res.next()) {
          count += res.getInt(1);
        }
        prestt.close();
      } catch (SQLException ex) {
        Logger.getLogger(InputJadwal.class.getName()).log(Level.SEVERE, null, ex);
      }
      return count;
    }
    
    private boolean checkGuruNotTakenMapel(String nidn, int mapel) {
      try {
        prestt = db.conn.prepareStatement("SELECT * FROM guru_mapel WHERE guru = ? AND mapel = ?");
        prestt.setString(1, nidn);
        prestt.setInt(2, mapel);
        ResultSet res = prestt.executeQuery();
        
        if (!res.isBeforeFirst()) {
          return true;
        }
        
        prestt.close();
        return false;
      } catch (SQLException ex) {
        Logger.getLogger(InputJadwal.class.getName()).log(Level.SEVERE, null, ex);
      }
      return false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    jComboBox1 = new javax.swing.JComboBox<>();
    jComboBox2 = new javax.swing.JComboBox<>();
    jButton1 = new javax.swing.JButton();
    jButton2 = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    jTable1 = new javax.swing.JTable();
    jButton3 = new javax.swing.JButton();
    jButton4 = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setSize(new java.awt.Dimension(768, 1024));

    jPanel1.setBackground(new java.awt.Color(204, 153, 255));

    jLabel1.setText("Input Guru Mapel");

    jLabel2.setText("nama guru");

    jLabel3.setText("Nama mapel");

    jButton1.setText("Submit");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    jButton2.setText("Hapus");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton2ActionPerformed(evt);
      }
    });

    jTable1.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null, null},
        {null, null, null},
        {null, null, null},
        {null, null, null}
      },
      new String [] {
        "id", "nama_guru", "nama_mapel"
      }
    ));
    jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        jTable1MousePressed(evt);
      }
    });
    jScrollPane1.setViewportView(jTable1);

    jButton3.setText("Back To Main Menu");
    jButton3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton3ActionPerformed(evt);
      }
    });

    jButton4.setText("Clear");
    jButton4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton4ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(143, 143, 143)
            .addComponent(jLabel1))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(21, 21, 21)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jComboBox1, 0, 217, Short.MAX_VALUE)
              .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(115, 115, 115)
            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(19, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGap(14, 14, 14)
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel3)
          .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jButton1)
          .addComponent(jButton2)
          .addComponent(jButton4))
        .addGap(18, 18, 18)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 98, Short.MAX_VALUE)
        .addComponent(jButton3)
        .addGap(56, 56, 56))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        if (idSelected != null) {
            boolean result = deleteData();
            if (result) {
                idSelected = null;
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus", "Berhasil",JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Data gagal dihapus", "Gagal",JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Belum ada data yang dipilih", "Gagal",JOptionPane.ERROR_MESSAGE);
        }
        newRecord();
        bacaTabel();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
        // TODO add your handling code here:
        int row = jTable1.getSelectedRow();
        idSelected = (String) jTable1.getValueAt(row, 0);
        if (idSelected != null) editData();
    }//GEN-LAST:event_jTable1MousePressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
      try {
        String guruData = jComboBox1.getSelectedItem().toString();
        String[] guruDataArray = guruData.split(" - ");
        String guruId = guruDataArray[0]; // Mendapatkan ID guru dari combo box
        
        String mapelData = jComboBox2.getSelectedItem().toString();
        String[] mapelDataArray = mapelData.split(" - ");
        String mapelId = mapelDataArray[0]; // Mendapatkan ID mapel dari combo box
        
        if (
            (
              checkMapelNumber(guruId) < 3 ||
              idSelected != null
            ) &&
            checkGuruNotTakenMapel(guruId, Integer.parseInt(mapelId))
        ) {
          if (idSelected != null) {
              prestt = db.conn.prepareStatement(
                  "UPDATE guru_mapel SET guru = ?, mapel = ? WHERE id = ?"
              );

              prestt.setString(1, guruId);
              prestt.setString(2, mapelId);
              prestt.setString(3, idSelected);
          } else {
              prestt = db.conn.prepareStatement("INSERT INTO guru_mapel (guru, mapel) VALUES (?, ?)");
              prestt.setString(1, guruId);
              prestt.setString(2, mapelId);
          }

          int res = prestt.executeUpdate();

          if (res == 1) {
              JOptionPane.showMessageDialog(this, "Data berhasil " + (idSelected != null ? "diubah" : "ditambah"), "Berhasil",JOptionPane.INFORMATION_MESSAGE);
          } else {
              JOptionPane.showMessageDialog(this, "Data gagal " + (idSelected != null ? "diubah" : "ditambah"), "Gagal",JOptionPane.ERROR_MESSAGE);
          }

          prestt.close();
        } else {
          JOptionPane.showMessageDialog(this, "Data gagal ditambah karena tidak bisa diberi lebih dari 3 mapel pada 1 guru atau guru telah mengambil mapel", "Gagal",JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    newRecord();
    bacaTabel();
    idSelected = null;
    }//GEN-LAST:event_jButton1ActionPerformed

  private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    dispose();
  }//GEN-LAST:event_jButton3ActionPerformed

  private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
    newRecord();
  }//GEN-LAST:event_jButton4ActionPerformed

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
            java.util.logging.Logger.getLogger(InputGuruMapel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InputGuruMapel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InputGuruMapel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InputGuruMapel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new InputGuruMapel().setVisible(true);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(InputGuruMapel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(InputGuruMapel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    private Statement stmt;
    private PreparedStatement prestt;
    DBConnection db = new DBConnection();
    
    private String idSelected = null;

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton2;
  private javax.swing.JButton jButton3;
  private javax.swing.JButton jButton4;
  private javax.swing.JComboBox<String> jComboBox1;
  private javax.swing.JComboBox<String> jComboBox2;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTable jTable1;
  // End of variables declaration//GEN-END:variables
}
