package fidebank.operation;

import fidebank.controller.Sistema;

public class DepositoOperacion implements Operacion {
    private final double monto;
    public DepositoOperacion(double monto) { this.monto = monto; }

    @Override
    public void ejecutar() { Sistema.depositar(monto); }
}
