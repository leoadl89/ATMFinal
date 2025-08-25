package fidebank.operation;

import fidebank.controller.Sistema;
import fidebank.exception.PinInvalidoException;

public class CambioPinOperacion implements Operacion {

    private final String pinActual;
    private final String pinNuevo;

    public CambioPinOperacion(String pinActual, String pinNuevo) {
        this.pinActual = pinActual;
        this.pinNuevo  = pinNuevo;
    }

    @Override
    public void ejecutar() {
        try {
            Sistema.cambiarPin(pinActual, pinNuevo);
        } catch (PinInvalidoException e) {
            // Mantener la interfaz Operacion sin checked exceptions
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "CambioPinOperacion{pinActual=****, pinNuevo=****}";
    }
}
