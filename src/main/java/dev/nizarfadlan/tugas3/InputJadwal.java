/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package dev.nizarfadlan.tugas3;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author nizar
 */
public class InputJadwal extends javax.swing.JFrame {

  /**
   * Creates new form InputJadwal
   * @throws java.lang.ClassNotFoundException
   * @throws java.sql.SQLException
   */
  public InputJadwal() throws ClassNotFoundException, SQLException {
    initComponents();
    db.connection();
    this.setTitle("Form Jadwal - Program Penjadwalan SMP");
    bacaMapel();
    bacaData();
  }
  
  /**
   * Function Utils
   */
  private void bacaMapel() {
    try {
      stmt = db.openConnection();
      ResultSet res = stmt.executeQuery("SELECT * FROM mapel");
      while (res.next()) {
        String id = String.valueOf(res.getInt("id"));
        String nama = res.getString("nama");
        
        String mapel = id + delimiter + nama;
        comboMapel.addItem(mapel);
      }

      stmt.close();
    } catch (SQLException ex) {
      Logger.getLogger(InputJadwal.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  private void newRecord() {
    try {
      comboHari.setSelectedIndex(0);
      comboKelas.setSelectedIndex(0);
      comboMapel.setSelectedIndex(0);
      
      Date resetTime = sdf.parse("00:00:00");
      fieldWaktu.setValue(resetTime);
      idSelected = null;
    } catch (ParseException ex) {
      Logger.getLogger(InputJadwal.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  private String getTime(String dateTime) {
    String regex = "\\d{2}:\\d{2}:\\d{2}";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(dateTime);
    
    return matcher.find() ? matcher.group() : "";
  }
  
  private int getIdMapel(String mapel) {
    return Integer.parseInt(mapel.split(delimiter)[0]);
  }
  
  private Time longTimeMapel(Time startMapel, int longTime) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(startMapel);
    cal.add(Calendar.MINUTE, longTime);

    Date newDate = cal.getTime();
    Time endTimeMapel = new Time(newDate.getTime());
    
    return endTimeMapel;
  }
  
  private boolean noSameTime(String hari, Time jam) {
    try {
      prestt = db.conn.prepareStatement(
              "SELECT * FROM penjadwalan p INNER JOIN mapel m ON p.mapel = m.id WHERE p.hari = ?"
      );
      prestt.setString(1, hari);
      ResultSet res = prestt.executeQuery();
      
      while(res.next()) {
        Time startTimeMapel = res.getTime("jam");
        int lamaPelajaran = res.getInt("jam_pelajaran");
        
        Time endTimeMapel = longTimeMapel(jam, lamaPelajaran);
        
        if (jam.compareTo(startTimeMapel) >= 0 && jam.compareTo(endTimeMapel) <= 0) {
          return false;
        }
      }
      
      return true;
    } catch (SQLException ex) {
      Logger.getLogger(InputJadwal.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    }
  }
  
  private boolean notInTheSameClass(int idMapel, String kelas) {
    try {
      prestt = db.conn.prepareStatement(
  "SELECT * FROM penjadwalan p INNER JOIN mapel m ON p.mapel = m.id WHERE p.mapel = ? AND p.kelas = ? LIMIT 1"
      );
      prestt.setInt(1, idMapel);
      prestt.setString(2, kelas);
      ResultSet res = prestt.executeQuery();
      boolean resBoolean = !res.isBeforeFirst();
      
      prestt.close();
      return resBoolean;
    } catch (SQLException ex) {
      Logger.getLogger(InputJadwal.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    }
  }
  
  private boolean noSchedule(int idMapel, String kelas, String hari, Time jam) {
    try {
      prestt = db.conn.prepareStatement(
  "SELECT * FROM penjadwalan p INNER JOIN mapel m ON p.mapel = m.id WHERE p.mapel = ? AND p.hari = ? LIMIT 1"
      );
      prestt.setInt(1, idMapel);
      prestt.setString(2, hari);
      ResultSet res = prestt.executeQuery();
      
      if (!res.isBeforeFirst()) {
        if (notInTheSameClass(idMapel, kelas)) {
          return noSameTime(hari, jam);
        }
      }
      
      prestt.close();
      return false;
    } catch (SQLException ex) {
      Logger.getLogger(InputJadwal.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    }
  }
  
  /**
   * Function Main
   */
  private void bacaData() {
    try {
      stmt = db.openConnection();
      ResultSet res = stmt.executeQuery("SELECT * FROM penjadwalan p INNER JOIN mapel m ON p.mapel = m.id");
      DefaultTableModel modelTable = (DefaultTableModel) tableJadwal.getModel();
      modelTable.setRowCount(0);
      while (res.next()) {
        String id = String.valueOf(res.getInt("id"));
        String nama = res.getString("nama");
        String kelas = res.getString("kelas");
        String hari = res.getString("hari");
        Time waktu = res.getTime("jam");
        String waktu_string = waktu.toString();
        int waktu_mapel = res.getInt("m.jam_pelajaran");
        
        Time endTimeMapel = longTimeMapel(waktu, waktu_mapel);
        
        String jadwal = waktu_string + "-" + endTimeMapel.toString();
        String[] dataListTemp = {id, nama, kelas, hari, jadwal};
        modelTable.addRow(dataListTemp);
      }

      stmt.close();
    } catch (SQLException ex) {
      Logger.getLogger(InputJadwal.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  private boolean deleteData() {
    try {
      prestt = db.conn.prepareStatement("DELETE FROM penjadwalan WHERE id = ?");
      prestt.setString(1, idSelected);
      int res = prestt.executeUpdate();
      prestt.close();

      return res == 1;
    } catch (SQLException ex) {
      Logger.getLogger(InputJadwal.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    }
  }
  
  private boolean submitData(String mapel, String kelas, String hari, String waktu) {
    try {
      int idMapel = getIdMapel(mapel);
      Date date = null;
      try {
        date = sdf.parse(waktu);
      } catch (ParseException ex) {
        Logger.getLogger(InputJadwal.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      Time time = new Time(date.getTime());

      if (idSelected != null) {
        prestt = db.conn.prepareStatement(
     "UPDATE penjadwalan SET mapel = ?, kelas = ?, hari = ?, jam = ? WHERE id = ?"
        );

        prestt.setInt(1, idMapel);
        prestt.setString(2, kelas);
        prestt.setString(3, hari);
        prestt.setTime(4, time);
        prestt.setString(5, idSelected);
      } else {
        prestt = db.conn.prepareStatement("INSERT INTO penjadwalan (mapel, kelas, hari, jam) VALUES(?,?,?,?)");
        prestt.setInt(1, idMapel);
        prestt.setString(2, kelas);
        prestt.setString(3, hari);
        prestt.setTime(4, time);
      }

      int res = prestt.executeUpdate();
      
      prestt.close();
      return res == 1;
    } catch (SQLException e) {
      Logger.getLogger(InputJadwal.class.getName()).log(Level.SEVERE, null, e);
    }
    return false;
  }
  
  private void editData() {
    try {
      prestt = db.conn.prepareStatement("SELECT * FROM penjadwalan p INNER JOIN mapel m ON p.mapel = m.id WHERE p.id = ? LIMIT 1");
      prestt.setString(1, idSelected);
      ResultSet res = prestt.executeQuery();

      while (res.next()) {
        comboMapel.setSelectedItem(res.getInt("mapel") + delimiter + res.getString("nama"));
        comboKelas.setSelectedItem(res.getString("kelas"));
        comboHari.setSelectedItem(res.getString("hari"));
        fieldWaktu.setValue(res.getTime("jam"));
      }

      prestt.close();
    } catch (SQLException ex) {
      Logger.getLogger(InputJadwal.class.getName()).log(Level.SEVERE, null, ex);
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

    jPanel1 = new javax.swing.JPanel();
    labelJadwal = new javax.swing.JLabel();
    labelMapel = new javax.swing.JLabel();
    comboMapel = new javax.swing.JComboBox<>();
    labelKelas = new javax.swing.JLabel();
    comboKelas = new javax.swing.JComboBox<>();
    labelHari = new javax.swing.JLabel();
    labelWaktu = new javax.swing.JLabel();
    Date date = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    date = calendar.getTime();
    SpinnerDateModel sm = new SpinnerDateModel(date, null, null, Calendar.HOUR_OF_DAY);
    fieldWaktu = new javax.swing.JSpinner(sm);
    comboHari = new javax.swing.JComboBox<>();
    buttonSubmit = new javax.swing.JButton();
    buttonHapus = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableJadwal = new javax.swing.JTable();
    jButton1 = new javax.swing.JButton();
    buttonClear = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setSize(new java.awt.Dimension(768, 1024));

    jPanel1.setBackground(new java.awt.Color(204, 153, 255));

    labelJadwal.setFont(new java.awt.Font("SF Pro Display", 0, 13)); // NOI18N
    labelJadwal.setText("FORM INPUT JADWAL");

    labelMapel.setText("Mapel");

    labelKelas.setText("Kelas");

    comboKelas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3" }));

    labelHari.setText("Hari");

    labelWaktu.setText("Waktu");

    JSpinner.DateEditor de = new JSpinner.DateEditor(fieldWaktu, "HH:mm:ss");
    fieldWaktu.setEditor(de);

    comboHari.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Senin", "Selasa", "Rabu", "Kamis", "Jumat" }));

    buttonSubmit.setText("Submit");
    buttonSubmit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonSubmitActionPerformed(evt);
      }
    });

    buttonHapus.setText("Hapus");
    buttonHapus.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonHapusActionPerformed(evt);
      }
    });

    tableJadwal.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Id", "Mapel", "Kelas", "Hari", "Jadwal"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
      };
      boolean[] canEdit = new boolean [] {
        false, false, false, false, false
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    tableJadwal.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        tableJadwalMousePressed(evt);
      }
    });
    jScrollPane1.setViewportView(tableJadwal);
    if (tableJadwal.getColumnModel().getColumnCount() > 0) {
      tableJadwal.getColumnModel().getColumn(0).setResizable(false);
      tableJadwal.getColumnModel().getColumn(0).setPreferredWidth(0);
      tableJadwal.getColumnModel().getColumn(1).setResizable(false);
      tableJadwal.getColumnModel().getColumn(1).setPreferredWidth(0);
      tableJadwal.getColumnModel().getColumn(2).setResizable(false);
      tableJadwal.getColumnModel().getColumn(2).setPreferredWidth(0);
      tableJadwal.getColumnModel().getColumn(3).setResizable(false);
      tableJadwal.getColumnModel().getColumn(3).setPreferredWidth(0);
    }

    jButton1.setText("Back to main menu");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    buttonClear.setText("Clear");
    buttonClear.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonClearActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(26, 26, 26)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(labelMapel)
              .addComponent(labelHari)))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(buttonClear, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addGap(50, 50, 50)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(comboMapel, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(comboKelas, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(fieldWaktu)
          .addComponent(comboHari, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(buttonHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addGap(33, 33, 33))
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(26, 26, 26)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(labelWaktu)
              .addComponent(labelKelas)))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(131, 131, 131)
            .addComponent(jButton1)))
        .addGap(0, 13, Short.MAX_VALUE))
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGap(129, 129, 129)
        .addComponent(labelJadwal)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGap(21, 21, 21)
        .addComponent(labelJadwal)
        .addGap(26, 26, 26)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelMapel)
          .addComponent(comboMapel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(28, 28, 28)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelKelas)
          .addComponent(comboKelas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(31, 31, 31)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelHari, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(comboHari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(28, 28, 28)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelWaktu)
          .addComponent(fieldWaktu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(32, 32, 32)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonSubmit)
          .addComponent(buttonHapus)
          .addComponent(buttonClear))
        .addGap(18, 18, 18)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(18, 18, 18)
        .addComponent(jButton1)
        .addGap(15, 15, 15))
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

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    dispose();
  }//GEN-LAST:event_jButton1ActionPerformed

  private void buttonHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonHapusActionPerformed
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
    bacaData();
  }//GEN-LAST:event_buttonHapusActionPerformed

  private void buttonSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSubmitActionPerformed
    String mapel = comboMapel.getSelectedItem().toString();
    String kelas = comboKelas.getSelectedItem().toString();
    String hari = comboHari.getSelectedItem().toString();
    String waktu = getTime(fieldWaktu.getValue().toString());
    
    Time time = Time.valueOf(waktu);
    boolean resNoSchedule = noSchedule(getIdMapel(mapel), kelas, hari, time);
    
    if (resNoSchedule || idSelected != null) {
      boolean result = submitData(mapel, kelas, hari, waktu);
    
      if (result) {
        JOptionPane.showMessageDialog(this, "Data berhasil " + (idSelected != null ? "diubah" : "ditambah"), "Berhasil",JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(this, "Data gagal " + (idSelected != null ? "diubah" : "ditambah"), "Gagal",JOptionPane.ERROR_MESSAGE);
      }
    } else {
      JOptionPane.showMessageDialog(this, 
              "Data gagal " + (idSelected != null ? "diubah" : "ditambah") + ":" +
              "\n- Mapel sudah diinputkan pada kelas tersebut\n- Ada waktu yang bertabrakan dengan mapel lain\n- Mapel sudah diinput dihari itu", "Gagal",JOptionPane.ERROR_MESSAGE);
    }
    
    newRecord();
    bacaData();
  }//GEN-LAST:event_buttonSubmitActionPerformed

  private void tableJadwalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableJadwalMousePressed
    int row = tableJadwal.getSelectedRow();
    idSelected = (String) tableJadwal.getValueAt(row, 0);
    if (idSelected != null) editData();
  }//GEN-LAST:event_tableJadwalMousePressed

  private void buttonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClearActionPerformed
    newRecord();
  }//GEN-LAST:event_buttonClearActionPerformed

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
      java.util.logging.Logger.getLogger(InputJadwal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(InputJadwal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(InputJadwal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(InputJadwal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          new InputJadwal().setVisible(true);
        } catch (ClassNotFoundException | SQLException ex) {
          Logger.getLogger(InputJadwal.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    });
  }
  
  DBConnection db = new DBConnection();

  private Statement stmt;
  private PreparedStatement prestt;

  private String idSelected = null;
  private String delimiter = "-";
  private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonClear;
  private javax.swing.JButton buttonHapus;
  private javax.swing.JButton buttonSubmit;
  private javax.swing.JComboBox<String> comboHari;
  private javax.swing.JComboBox<String> comboKelas;
  private javax.swing.JComboBox<String> comboMapel;
  private javax.swing.JSpinner fieldWaktu;
  private javax.swing.JButton jButton1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JLabel labelHari;
  private javax.swing.JLabel labelJadwal;
  private javax.swing.JLabel labelKelas;
  private javax.swing.JLabel labelMapel;
  private javax.swing.JLabel labelWaktu;
  private javax.swing.JTable tableJadwal;
  // End of variables declaration//GEN-END:variables
}
