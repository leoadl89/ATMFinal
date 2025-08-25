package fidebank.model;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Persona {
    private String pin;
    private final List<Cuenta> cuentas = new ArrayList<>();

    public Cliente(String nombre, String apellido, String pin) {
        super(nombre, apellido);
        this.pin = pin;
    }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public boolean validarPIN(String intento) {
        return intento != null && intento.equals(pin);
    }

    public List<Cuenta> getCuentas() { return cuentas; }

    public Cuenta getCuentaPrincipal() {
        return cuentas.isEmpty() ? null : cuentas.get(0);
    }
}
