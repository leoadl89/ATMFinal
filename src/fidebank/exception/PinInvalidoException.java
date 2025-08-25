package fidebank.exception;

public class PinInvalidoException extends Exception {
    public PinInvalidoException() { super("PIN invalido"); }
    public PinInvalidoException(String msg) { super(msg); }
}
