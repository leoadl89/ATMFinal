package fidebank.model;

import java.time.LocalDateTime;

public class Transaccion {
    private String tipo;
    private double monto;
    private String cuentaOrigen;
    private String cuentaDestino;
    private LocalDateTime fecha = LocalDateTime.now();

    public Transaccion(String tipo, double monto) {
        this.tipo = tipo;
        this.monto = monto;
    }

    public Transaccion(String tipo, double monto, String origen, String destino) {
        this.tipo = tipo;
        this.monto = monto;
        this.cuentaOrigen = origen;
        this.cuentaDestino = destino;
    }

    public String getTipo() { return tipo; }
    public double getMonto() { return monto; }
    public String getCuentaOrigen() { return cuentaOrigen; }
    public String getCuentaDestino() { return cuentaDestino; }
    public LocalDateTime getFecha() { return fecha; }
}
