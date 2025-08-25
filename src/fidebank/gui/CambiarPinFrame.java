package fidebank.gui;

import fidebank.controller.Sistema;
import fidebank.exception.PinInvalidoException;

import javax.swing.*;
import java.awt.*;

public class CambiarPinFrame extends JFrame {
    public CambiarPinFrame() {
        setTitle("Cambiar PIN");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(320, 220);
        setLocationRelativeTo(null);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JPasswordField act = new JPasswordField();
        JPasswordField nuevo = new JPasswordField();
        JPasswordField conf = new JPasswordField();

        JLabel l1 = new JLabel("PIN actual:");    act.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel l2 = new JLabel("PIN nuevo:");     nuevo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel l3 = new JLabel("Confirmar PIN:"); conf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JButton b = new JButton("Actualizar");
        b.addActionListener(e -> {
            String a = new String(act.getPassword());
            String n = new String(nuevo.getPassword());
            String c = new String(conf.getPassword());
            if (!n.equals(c)) { JOptionPane.showMessageDialog(this, "El PIN nuevo no coincide"); return; }
            try {
                Sistema.cambiarPin(a, n);
                JOptionPane.showMessageDialog(this, "PIN actualizado");
                dispose();
            } catch (PinInvalidoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        p.add(l1); p.add(act);
        p.add(l2); p.add(nuevo);
        p.add(l3); p.add(conf);
        p.add(Box.createVerticalStrut(10));
        p.add(b);
        add(p);
    }
}
