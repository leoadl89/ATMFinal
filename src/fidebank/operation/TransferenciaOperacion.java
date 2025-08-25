package fidebank.operation;

import fidebank.controller.Sistema;
import fidebank.exception.SaldoInsuficienteException;

public class TransferenciaOperacion implements Operacion {
    private final String destinoNumero;
    private final double monto;

    public TransferenciaOperacion(String destinoNumero, double monto) {
        this.destinoNumero = destinoNumero;
        this.monto = monto;
    }

    @Override
    public void ejecutar() {
        try {
            Sistema.transferir(destinoNumero, monto);
        } catch (SaldoInsuficienteException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
