package dao;

import fidebank.model.Cliente;
import fidebank.model.Cuenta;

import java.sql.*;
import java.math.BigDecimal;

public class CuentaDAO {

    public static Cliente loginPorPin(String pin) throws SQLException {
        String sql = "SELECT numero, nombre, apellido, saldo FROM cuenta WHERE pin=? LIMIT 1";
        try (Connection cn = ConexionBD.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, pin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int numero = rs.getInt("numero");
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");
                    BigDecimal saldo = rs.getBigDecimal("saldo");
                    Cliente c = new Cliente(nombre, apellido, pin);
                    c.getCuentas().clear();
                    c.getCuentas().add(new Cuenta(String.valueOf(numero), saldo.doubleValue()));
                    return c;
                }
            }
        }
        return null;
    }

    public static Double consultarSaldo(int numero) throws SQLException {
        String sql = "SELECT saldo FROM cuenta WHERE numero=?";
        try (Connection cn = ConexionBD.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1).doubleValue();
            }
        }
        return null;
    }

    public static boolean depositar(int numero, double monto) throws SQLException {
        if (monto <= 0) return false;
        String up = "UPDATE cuenta SET saldo = saldo + ? WHERE numero=?";
        try (Connection cn = ConexionBD.get();
             PreparedStatement ps = cn.prepareStatement(up)) {
            ps.setBigDecimal(1, new BigDecimal(String.valueOf(monto)));
            ps.setInt(2, numero);
            int n = ps.executeUpdate();
            if (n == 1) {
                registrarMovimiento(cn, "DEPOSITO", monto, numero, null, consultarSaldo(cn, numero), "Deposito de colones");
                return true;
            }
        }
        return false;
    }

    public static boolean retirar(int numero, double monto) throws SQLException {
        if (monto <= 0) return false;
        String up = "UPDATE cuenta SET saldo = saldo - ? WHERE numero=? AND saldo >= ?";
        try (Connection cn = ConexionBD.get();
             PreparedStatement ps = cn.prepareStatement(up)) {
            ps.setBigDecimal(1, new BigDecimal(String.valueOf(monto)));
            ps.setInt(2, numero);
            ps.setBigDecimal(3, new BigDecimal(String.valueOf(monto)));
            int n = ps.executeUpdate();
            if (n == 1) {
                registrarMovimiento(cn, "RETIRO", monto, numero, null, consultarSaldo(cn, numero), "Retiro de colones");
                return true;
            }
        }
        return false;
    }

    public static boolean transferir(int origen, int destino, double monto) throws SQLException {
        if (monto <= 0 || origen == destino) return false;
        try (Connection cn = ConexionBD.get()) {
            cn.setAutoCommit(false);
            try {
                // valida destino
                try (PreparedStatement ex = cn.prepareStatement("SELECT 1 FROM cuenta WHERE numero=?")) {
                    ex.setInt(1, destino);
                    try (ResultSet rs = ex.executeQuery()) {
                        if (!rs.next()) { cn.rollback(); return false; }
                    }
                }
                // debitar origen
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE cuenta SET saldo = saldo - ? WHERE numero=? AND saldo >= ?")) {
                    ps.setBigDecimal(1, new BigDecimal(String.valueOf(monto)));
                    ps.setInt(2, origen);
                    ps.setBigDecimal(3, new BigDecimal(String.valueOf(monto)));
                    if (ps.executeUpdate() != 1) { cn.rollback(); return false; }
                }
                // acreditar destino
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE cuenta SET saldo = saldo + ? WHERE numero=?")) {
                    ps.setBigDecimal(1, new BigDecimal(String.valueOf(monto)));
                    ps.setInt(2, destino);
                    if (ps.executeUpdate() != 1) { cn.rollback(); return false; }
                }
                // movimiento con saldo resultante en origen
                Double saldoOrigen = consultarSaldo(cn, origen);
                registrarMovimiento(cn, "TRANSFERENCIA", monto, origen, destino, saldoOrigen,
                        "Envio a cuenta " + destino);
                cn.commit();
                return true;
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    public static boolean cambiarPin(int numero, String actual, String nuevo) throws SQLException {
        String up = "UPDATE cuenta SET pin=? WHERE numero=? AND pin=?";
        try (Connection cn = ConexionBD.get();
             PreparedStatement ps = cn.prepareStatement(up)) {
            ps.setString(1, nuevo);
            ps.setInt(2, numero);
            ps.setString(3, actual);
            return ps.executeUpdate() == 1;
        }
    }

    public static Integer registrarCuenta(String nombre, String apellido, String pin, double saldoInicial) throws SQLException {
        String ins = "INSERT INTO cuenta(nombre,apellido,pin,saldo) VALUES(?,?,?,?)";
        try (Connection cn = ConexionBD.get();
             PreparedStatement ps = cn.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, pin);
            ps.setBigDecimal(4, new BigDecimal(String.valueOf(saldoInicial)));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return null;
    }

    public static String nombreTitular(int numero) throws SQLException {
        String sql = "SELECT CONCAT_WS(' ', nombre, apellido) FROM cuenta WHERE numero=?";
        try (Connection cn = ConexionBD.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return null;
    }

    private static Double consultarSaldo(Connection cn, int numero) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("SELECT saldo FROM cuenta WHERE numero=?")) {
            ps.setInt(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1).doubleValue();
            }
        }
        return null;
    }

    private static void registrarMovimiento(Connection cn, String tipo, double monto,
                                            Integer origen, Integer destino, Double saldoRes, String detalle) throws SQLException {
        String ins = "INSERT INTO movimiento(tipo,monto,cuenta_origen,cuenta_destino,saldo_resultante,detalle) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = cn.prepareStatement(ins)) {
            ps.setString(1, tipo);
            ps.setBigDecimal(2, new BigDecimal(String.valueOf(monto)));
            if (origen == null)  ps.setNull(3, Types.INTEGER); else ps.setInt(3, origen);
            if (destino == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, destino);
            if (saldoRes == null) ps.setNull(5, Types.DECIMAL); else ps.setBigDecimal(5, new BigDecimal(String.valueOf(saldoRes)));
            ps.setString(6, detalle);
            ps.executeUpdate();
        }
    }
}
