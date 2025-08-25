package fidebank.gui;

import fidebank.controller.Sistema;
import fidebank.model.Transaccion;

import javax.swing.*;
import java.awt.*;

public class DepositoFrame extends JFrame {
    public DepositoFrame() {
        setTitle("Depositar");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(320, 200);
        setLocationRelativeTo(null);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel l = new JLabel("Monto a depositar (₡):");
        JTextField t = new JTextField();
        t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JButton b = new JButton("Depositar");

        b.addActionListener(e -> {
            String s = t.getText().trim();
            if (s.isEmpty()) { JOptionPane.showMessageDialog(this, "Digite un monto"); return; }
            try {
                double m = Double.parseDouble(s);
                int ok = JOptionPane.showConfirmDialog(this, "Inserte el efectivo y confirme", "Deposito", JOptionPane.OK_CANCEL_OPTION);
                if (ok != JOptionPane.OK_OPTION) return;
                Sistema.depositar(m);
                double nuevo = Sistema.consultarSaldoActual();
                Transaccion tx = new Transaccion("DEPOSITO", m, Sistema.getNumeroCuentaActual(), null);
                new ComprobanteFrame(tx, nuevo).setVisible(true);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Monto invalido");
            }
        });

        p.add(l); p.add(t);
        p.add(Box.createVerticalStrut(12));
        p.add(b);
        add(p);
    }
}
