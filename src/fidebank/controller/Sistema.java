package fidebank.controller;

import fidebank.model.Cliente;
import fidebank.model.Cuenta;
import fidebank.exception.PinInvalidoException;
import fidebank.exception.SaldoInsuficienteException;
import dao.CuentaDAO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


// Prefiere MySQL; CSV como respaldo si la DB no esta disponible.

public class Sistema {
    private static List<Cliente> clientes = new ArrayList<>();
    private static Cliente clienteActual;
    private static final String RUTA_CSV = "clientes.csv";
    private static final boolean USAR_DB = true; // <--- BD activa

    // -------- Autenticacion --
    public static void login(String pin) throws PinInvalidoException {
        if (USAR_DB) {
            try {
                Cliente c = CuentaDAO.loginPorPin(pin);
                if (c != null) { clienteActual = c; return; }
            } catch (Exception e) { throw new PinInvalidoException(); }
            throw new PinInvalidoException();
        } else {
            for (Cliente c : clientes) if (c.validarPIN(pin)) { clienteActual = c; return; }
            throw new PinInvalidoException();
        }
    }

    public static Cliente getClienteActual() { return clienteActual; }

    private static Cuenta cuentaPrincipal() {
        if (clienteActual == null) return null;
        return clienteActual.getCuentaPrincipal();
    }

    public static String getNumeroCuentaActual() {
        Cuenta cp = cuentaPrincipal();
        return cp != null ? cp.getNumeroCuenta() : null;
    }

    // ------ Operaciones --------
    public static double consultarSaldoActual() {
        if (USAR_DB) {
            try {
                Integer nro = Integer.valueOf(getNumeroCuentaActual());
                Double s = CuentaDAO.consultarSaldo(nro);
                if (s != null) {
                    cuentaPrincipal().setSaldo(s);
                    return s;
                }
            } catch (Exception ignored) {}
            return cuentaPrincipal().getSaldo();
        } else {
            return cuentaPrincipal().getSaldo();
        }
    }

    public static void depositar(double monto) {
        if (USAR_DB) {
            try {
                Integer nro = Integer.valueOf(getNumeroCuentaActual());
                if (CuentaDAO.depositar(nro, monto)) {
                    Double s = CuentaDAO.consultarSaldo(nro);
                    if (s != null) cuentaPrincipal().setSaldo(s);
                }
            } catch (Exception e) { throw new RuntimeException(e); }
        } else {
            cuentaPrincipal().depositar(monto);
        }
    }

    public static void retirar(double monto) throws SaldoInsuficienteException {
        if (USAR_DB) {
            try {
                Integer nro = Integer.valueOf(getNumeroCuentaActual());
                boolean ok = CuentaDAO.retirar(nro, monto);
                if (!ok) throw new SaldoInsuficienteException("Saldo insuficiente para retirar ₡" + monto);
                Double s = CuentaDAO.consultarSaldo(nro);
                if (s != null) cuentaPrincipal().setSaldo(s);
            } catch (SaldoInsuficienteException se) { throw se; }
              catch (Exception e) { throw new RuntimeException(e); }
        } else {
            if (!cuentaPrincipal().retirar(monto)) {
                throw new SaldoInsuficienteException("Saldo insuficiente para retirar ₡" + monto);
            }
        }
    }

    public static void transferir(String numeroDestino, double monto) throws SaldoInsuficienteException {
        if (USAR_DB) {
            try {
                int origen  = Integer.parseInt(getNumeroCuentaActual());
                int destino = Integer.parseInt(numeroDestino);
                boolean ok = CuentaDAO.transferir(origen, destino, monto);
                if (!ok) throw new SaldoInsuficienteException("No fue posible transferir ₡" + monto);
                Double s = CuentaDAO.consultarSaldo(origen);
                if (s != null) cuentaPrincipal().setSaldo(s);
            } catch (SaldoInsuficienteException se) { throw se; }
              catch (Exception e) { throw new RuntimeException(e); }
        } else {
            if (!cuentaPrincipal().retirar(monto)) {
                throw new SaldoInsuficienteException("Saldo insuficiente para transferencia ₡" + monto);
            }
            Cliente dest = buscarClientePorCuenta(numeroDestino);
            if (dest != null && dest.getCuentaPrincipal() != null) {
                dest.getCuentaPrincipal().depositar(monto);
            }
        }
    }

    public static void cambiarPin(String pinActual, String pinNuevo) throws PinInvalidoException {
        if (USAR_DB) {
            try {
                int nro = Integer.parseInt(getNumeroCuentaActual());
                boolean ok = CuentaDAO.cambiarPin(nro, pinActual, pinNuevo);
                if (!ok) throw new PinInvalidoException();
                clienteActual.setPin(pinNuevo);
            } catch (PinInvalidoException pe) { throw pe; }
              catch (Exception e) { throw new RuntimeException(e); }
        } else {
            if (!clienteActual.validarPIN(pinActual)) throw new PinInvalidoException();
            clienteActual.setPin(pinNuevo);
        }
    }

    public static Integer registrarCliente(String nombre, String apellido, String pin) {
        if (USAR_DB) {
            try {
                return CuentaDAO.registrarCuenta(nombre, apellido, pin, 0.0);
            } catch (Exception e) { throw new RuntimeException(e); }
        } else {
            Cliente c = new Cliente(nombre, apellido, pin);
            c.getCuentas().clear();
            c.getCuentas().add(new Cuenta(generarNuevoNumeroCuenta(), 0.0));
            clientes.add(c);
            try { guardarDatosCSV(); } catch (IOException ignored) {}
            return Integer.valueOf(c.getCuentaPrincipal().getNumeroCuenta());
        }
    }

    public static Cliente buscarClientePorCuenta(String numeroCuenta) {
        if (USAR_DB) {
            try {
                int n = Integer.parseInt(numeroCuenta);
                String nom = CuentaDAO.nombreTitular(n);
                if (nom == null) return null;
                String[] parts = nom.split(" ", 2);
                String nombre = parts.length > 0 ? parts[0] : nom;
                String apellido = parts.length > 1 ? parts[1] : "";
                Cliente c = new Cliente(nombre, apellido, "***");
                Double s = CuentaDAO.consultarSaldo(n);
                c.getCuentas().clear();
                c.getCuentas().add(new Cuenta(numeroCuenta, s != null ? s : 0.0));
                return c;
            } catch (Exception e) { return null; }
        } else {
            for (Cliente c : clientes) {
                for (Cuenta cu : c.getCuentas()) {
                    if (cu.getNumeroCuenta().equals(numeroCuenta)) return c;
                }
            }
            return null;
        }
    }

    // -------- CSV respaldo --------
    private static String generarNuevoNumeroCuenta() {
        int max = 0;
        for (Cliente c : clientes) {
            for (Cuenta cu : c.getCuentas()) {
                try {
                    int n = Integer.parseInt(cu.getNumeroCuenta());
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("%06d", max + 1);
    }

    private static void cargarDatosCSV() throws IOException {
        clientes = new ArrayList<>();
        File f = new File(RUTA_CSV);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_CSV))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split(";");
                if (campos.length >= 5) {
                    String nombre = campos[0];
                    String apellido = campos[1];
                    String pin = campos[2];
                    String numCuenta = campos[3];
                    double saldo = Double.parseDouble(campos[4]);
                    Cliente c = new Cliente(nombre, apellido, pin);
                    c.getCuentas().clear();
                    c.getCuentas().add(new Cuenta(numCuenta, saldo));
                    clientes.add(c);
                }
            }
        }
    }

    private static void guardarDatosCSV() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_CSV))) {
            for (Cliente c : clientes) {
                for (Cuenta cu : c.getCuentas()) {
                    String linea = String.join(";",
                            c.getNombre(), c.getApellido(), c.getPin(),
                            cu.getNumeroCuenta(), String.valueOf(cu.getSaldo()));
                    bw.write(linea);
                    bw.newLine();
                }
            }
        }
    }
}
