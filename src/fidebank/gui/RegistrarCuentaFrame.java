package fidebank.gui;

import fidebank.controller.Sistema;
import fidebank.model.Transaccion;

import javax.swing.*;
import java.awt.*;

public class RegistrarCuentaFrame extends JFrame {
    public RegistrarCuentaFrame() {
        setTitle("Registrar cuenta");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(340, 240);
        setLocationRelativeTo(null);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JTextField nombre = new JTextField();
        JTextField apellido = new JTextField();
        JPasswordField pin = new JPasswordField();

        nombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        apellido.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        pin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        p.add(new JLabel("Nombre:"));   p.add(nombre);
        p.add(new JLabel("Apellido:")); p.add(apellido);
        p.add(new JLabel("PIN:"));      p.add(pin);

        JButton b = new JButton("Crear");
        b.addActionListener(e -> {
            String n = nombre.getText().trim();
            String a = apellido.getText().trim();
            String p1 = new String(pin.getPassword()).trim();
            if (n.isEmpty() || a.isEmpty() || p1.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos"); return;
            }
            Integer nueva = Sistema.registrarCliente(n, a, p1);
            String cuenta = (nueva == null) ? "(ver BD)" : String.valueOf(nueva);
            Transaccion tx = new Transaccion("APERTURA", 0.0, cuenta, null);
            new ComprobanteFrame(tx, 0.0).setVisible(true);
            dispose();
        });

        p.add(Box.createVerticalStrut(12));
        p.add(b);
        add(p);
    }
}
