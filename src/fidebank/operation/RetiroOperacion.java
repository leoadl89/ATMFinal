package fidebank.operation;

import fidebank.controller.Sistema;
import fidebank.exception.SaldoInsuficienteException;

public class RetiroOperacion implements Operacion {
    private final double monto;
    public RetiroOperacion(double monto) { this.monto = monto; }

    @Override
    public void ejecutar() {
        try {
            Sistema.retirar(monto);
        } catch (SaldoInsuficienteException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
