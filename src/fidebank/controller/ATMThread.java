package fidebank.controller;

import fidebank.gui.LoginFrame;

public class ATMThread extends Thread {
    @Override
    public void run() {
        new LoginFrame().setVisible(true);
    }
}
