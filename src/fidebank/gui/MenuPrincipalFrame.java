package fidebank.gui;

import fidebank.controller.Sistema;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipalFrame extends JFrame {

    public MenuPrincipalFrame() {
        setTitle("FideBank - Menu Principal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 360);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String nombre = Sistema.getClienteActual().nombreCompleto();
        String cuenta = Sistema.getNumeroCuentaActual();

        JLabel header = new JLabel("Bienvenido, " + nombre + " (Cuenta " + cuenta + ")", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(header, BorderLayout.NORTH);

        JPanel p = new JPanel(new GridLayout(3, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton bSaldo = new JButton("Consultar saldo");
        JButton bDep   = new JButton("Depositar");
        JButton bRet   = new JButton("Retirar");
        JButton bTrans = new JButton("Transferir");
        JButton bPin   = new JButton("Cambiar PIN");
        JButton bReg   = new JButton("Registrar cuenta");

        bSaldo.addActionListener(e -> new SaldoFrame().setVisible(true));
        bDep.addActionListener(e -> new DepositoFrame().setVisible(true));
        bRet.addActionListener(e -> new RetiroFrame().setVisible(true));
        bTrans.addActionListener(e -> new TransferenciaFrame().setVisible(true));
        bPin.addActionListener(e -> new CambiarPinFrame().setVisible(true));
        bReg.addActionListener(e -> new RegistrarCuentaFrame().setVisible(true));

        p.add(bSaldo); p.add(bDep); p.add(bRet);
        p.add(bTrans); p.add(bPin); p.add(bReg);

        add(p, BorderLayout.CENTER);

        JButton salir = new JButton("Salir");
        salir.addActionListener(e -> System.exit(0));
        add(salir, BorderLayout.SOUTH);
    }
}
