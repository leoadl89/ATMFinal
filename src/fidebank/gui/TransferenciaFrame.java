package fidebank.gui;

import javax.swing.*;
import java.awt.*;
import fidebank.controller.Sistema;
import fidebank.operation.TransferenciaOperacion;
import fidebank.model.Cliente;
import fidebank.model.Transaccion;

public class TransferenciaFrame extends JFrame {
    public TransferenciaFrame() {
        setTitle("Transferir");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(320, 220);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel l1 = new JLabel("Cuenta destino:");
        JTextField txtDestino = new JTextField();
        txtDestino.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JButton btn = new JButton("Continuar");
        btn.addActionListener(e -> {
            String numDestino = txtDestino.getText().trim();
            if (numDestino.isEmpty()) { JOptionPane.showMessageDialog(this, "Digite la cuenta destino"); return; }

            Cliente destino = Sistema.buscarClientePorCuenta(numDestino);
            if (destino == null) { JOptionPane.showMessageDialog(this, "Cuenta destino no existe"); return; }

            String nombreDest = destino.nombreCompleto();
            String input = JOptionPane.showInputDialog(this,
                "Titular: " + nombreDest + "\nMonto a transferir (₡):",
                "Transferir a " + nombreDest,
                JOptionPane.PLAIN_MESSAGE);
            if (input == null) return;

            try {
                double monto = Double.parseDouble(input);
                new TransferenciaOperacion(numDestino, monto).ejecutar();
                double nuevoSaldo = Sistema.consultarSaldoActual();
                Transaccion tx = new Transaccion("TRANSFERENCIA", monto, Sistema.getNumeroCuentaActual(), numDestino);
                new ComprobanteFrame(tx, nuevoSaldo).setVisible(true);
                dispose();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Monto invalido");
            } catch (RuntimeException ex1) {
                JOptionPane.showMessageDialog(this, ex1.getMessage());
            }
        });

        panel.add(l1); panel.add(txtDestino);
        panel.add(Box.createVerticalStrut(12));
        panel.add(btn);
        add(panel);
    }
}
