package fidebank.gui;

import fidebank.controller.Sistema;
import fidebank.exception.PinInvalidoException;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JPasswordField txtPin = new JPasswordField();

    public LoginFrame() {
        setTitle("FideBank - Ingresar PIN");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(360, 220);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 10));

        JPanel header = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel("FideBank", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.add(lbl, BorderLayout.CENTER);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel l2 = new JLabel("Ingrese su PIN:");
        txtPin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JButton btn = new JButton("Continuar");
        btn.addActionListener(e -> doLogin());

        form.add(l2); form.add(txtPin);
        form.add(Box.createVerticalStrut(12));
        form.add(btn);

        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
    }

    private void doLogin() {
        String pin = new String(txtPin.getPassword()).trim();
        if (pin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite su PIN");
            return;
        }
        try {
            Sistema.login(pin);
            new MenuPrincipalFrame().setVisible(true);
            dispose();
        } catch (PinInvalidoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
