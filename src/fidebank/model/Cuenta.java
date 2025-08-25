package fidebank.model;

public class Cuenta {
    private String numeroCuenta;
    private double saldo;

    public Cuenta(String numeroCuenta, double saldo) {
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
    }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public void depositar(double monto) {
        if (monto > 0) this.saldo += monto;
    }

    public boolean retirar(double monto) {
        if (monto > 0 && saldo >= monto) {
            this.saldo -= monto;
            return true;
        }
        return false;
    }
}
