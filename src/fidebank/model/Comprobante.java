package fidebank.model;

import java.time.format.DateTimeFormatter;

public class Comprobante {
    private final Transaccion tx;
    private final Double saldoNuevo; // puede ser null

    public Comprobante(Transaccion tx, Double saldoNuevo) {
        this.tx = tx;
        this.saldoNuevo = saldoNuevo;
    }

    public String generarTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append("FideBank - Comprobante\n");
        sb.append("Fecha: ").append(tx.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("Operacion: ").append(tx.getTipo()).append("\n");
        if (tx.getCuentaOrigen() != null)  sb.append("Cuenta: ").append(tx.getCuentaOrigen()).append("\n");
        if (tx.getCuentaDestino() != null) sb.append("Cuenta destino: ").append(tx.getCuentaDestino()).append("\n");
        sb.append("Monto: ₡").append(tx.getMonto()).append("\n");
        if (saldoNuevo != null) sb.append("Saldo nuevo: ₡").append(String.format("%.2f", saldoNuevo)).append("\n");
        sb.append("Gracias por usar FideBank");
        return sb.toString();
    }
}
