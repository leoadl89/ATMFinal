package fidebank.tools;

import dao.ConexionBD; // o fidebank.dao.ConexionBD según tu paquete
import java.sql.*;

public class QuickDBCheck {
  public static void main(String[] args) {
    try (Connection cn = ConexionBD.get();
         Statement st = cn.createStatement();
         ResultSet rs = st.executeQuery("SELECT numero,nombre,apellido,pin,saldo FROM fidebank.cuenta")) {
      while (rs.next()) {
        System.out.printf("Cuenta %d | %s %s | PIN=%s | Saldo=%.2f%n",
            rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getDouble(5));
      }
      System.out.println("Conexion OK");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}