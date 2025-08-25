package fidebank.gui;

import fidebank.controller.Sistema;
import fidebank.exception.SaldoInsuficienteException;
import fidebank.model.Transaccion;

import javax.swing.*;
import java.awt.*;

public class RetiroFrame extends JFrame {
    public RetiroFrame() {
        setTitle("Retirar");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(320, 200);
        setLocationRelativeTo(null);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel l = new JLabel("Monto a retirar (₡):");
        JTextField t = new JTextField();
        t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JButton b = new JButton("Retirar");

        b.addActionListener(e -> {
            String s = t.getText().trim();
            if (s.isEmpty()) { JOptionPane.showMessageDialog(this, "Digite un monto"); return; }
            try {
                double m = Double.parseDouble(s);
                Sistema.retirar(m);
                double nuevo = Sistema.consultarSaldoActual();
                Transaccion tx = new Transaccion("RETIRO", m, Sistema.getNumeroCuentaActual(), null);
                new ComprobanteFrame(tx, nuevo).setVisible(true);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Monto invalido");
            } catch (SaldoInsuficienteException sie) {
                JOptionPane.showMessageDialog(this, sie.getMessage());
            }
        });

        p.add(l); p.add(t);
        p.add(Box.createVerticalStrut(12));
        p.add(b);
        add(p);
    }
}
