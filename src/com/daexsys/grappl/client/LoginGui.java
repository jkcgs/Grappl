package com.daexsys.grappl.client;

import com.daexsys.grappl.GrapplGlobal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;

public class LoginGui extends JFrame {
    public JButton buttonLogin;
    public JButton buttonSignup;
    public JButton buttonAnonymous;
    public JButton buttonDonate;

    public JLabel labelUser;
    public JLabel labelPassword;

    public JTextField inputUser;
    public JPasswordField inputPassword;

    public LoginGui(){

        super(GrapplGlobal.APP_NAME + " Login");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        setSize(new Dimension(300, 240));
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // GUI Elements
        labelUser = new JLabel("Username");
        labelUser.setBounds(5, 5, 250, 20);
        add(labelUser);

        inputUser = new JTextField("");
        inputUser.setBounds(5, 25, 250, 20);
        add(inputUser);

        labelPassword = new JLabel("Password");
        labelPassword.setBounds(5, 45, 250, 20);
        add(labelPassword);

        inputPassword = new JPasswordField("");
        inputPassword.setBounds(5, 65, 250, 20);
        add(inputPassword);

        buttonLogin = new JButton("Login");
        buttonLogin.setBounds(2, 100, 140, 40);
        add(buttonLogin);

        buttonSignup = new JButton("Sign up");
        buttonSignup.setBounds(142, 100, 140, 40);
        buttonSignup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(URI.create("http://grappl.io/register"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        add(buttonSignup);

        buttonAnonymous = new JButton("Be Anonymous");
        buttonAnonymous.setBounds(2, 150, 192, 40);
        add(buttonAnonymous);

        buttonDonate = new JButton("Donate");
        buttonDonate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(URI.create("http://grappl.io/donate"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        buttonDonate.setBounds(201, 150, 75, 40);
        add(buttonDonate);

        setResizable(false);
    }
}
