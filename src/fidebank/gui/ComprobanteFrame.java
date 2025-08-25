package fidebank.gui;

import fidebank.model.Comprobante;
import fidebank.model.Transaccion;

import javax.swing.*;
import java.awt.*;

public class ComprobanteFrame extends JFrame {
    public ComprobanteFrame(Transaccion tx, Double saldoNuevo) {
        setTitle("Comprobante");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(360, 280);
        setLocationRelativeTo(null);

        JTextArea area = new JTextArea(new Comprobante(tx, saldoNuevo).generarTexto(), 12, 30);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(new JScrollPane(area));
    }
}
