package fidebank.gui;

import fidebank.controller.Sistema;
import fidebank.model.Comprobante;
import fidebank.model.Transaccion;

import javax.swing.*;
import java.awt.*;

public class SaldoFrame extends JFrame {
    public SaldoFrame() {
        setTitle("Consultar saldo");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(320, 180);
        setLocationRelativeTo(null);

        double saldo = Sistema.consultarSaldoActual();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel l = new JLabel("Saldo disponible: ₡" + String.format("%.2f", saldo), SwingConstants.CENTER);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton b = new JButton("Imprimir comprobante");
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.addActionListener(e -> {
            Transaccion tx = new Transaccion("CONSULTA_SALDO", 0.0);
            Comprobante comp = new Comprobante(tx, saldo);
            new ComprobanteFrame(tx, saldo).setVisible(true);
        });

        panel.add(l);
        panel.add(Box.createVerticalStrut(12));
        panel.add(b);

        add(panel);
    }
}
