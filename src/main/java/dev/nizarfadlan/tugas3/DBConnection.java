/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dev.nizarfadlan.tugas3;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author nizar
 */
public class DBConnection {
  private final String DB_URL = "jdbc:mysql://localhost:3306/tugas3_pag";
  private final String USER = "root";
  private final String PASS = "nizar";

  public Connection conn;
  public void connection() throws ClassNotFoundException, SQLException {
      DriverManager.registerDriver(new com.mysql.jdbc.Driver());
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
  }

  public Statement openConnection() throws SQLException{
      return conn.createStatement();
  }
}
