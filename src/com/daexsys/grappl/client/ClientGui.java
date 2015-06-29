package com.daexsys.grappl.client;

import com.daexsys.grappl.GrapplGlobal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGui extends JFrame {
    public JButton buttonClose;

    public JLabel labelAddress;
    public JLabel labelPort;
    public JLabel labelStatus;
    public JLabel labelClients;

    public ClientGui() {
        super(GrapplGlobal.APP_NAME + " Client");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        setSize(new Dimension(300, 240));
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        buttonClose = new JButton("Close " + GrapplGlobal.APP_NAME + " Client");
        buttonClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        buttonClose.setBounds(0, 95, 280, 100);
        add(buttonClose);

        // Status labels
        labelAddress = new JLabel("Global Address: ...");
        labelAddress.setBounds(5, 5, 450, 20);
        add(labelAddress);

        labelPort = new JLabel("Server on local port: ...");
        labelPort.setBounds(5, 25, 450, 20);
        add(labelPort);

        labelClients = new JLabel();
        labelClients.setBounds(5, 45, 450, 20);
        add(labelClients);

        labelStatus = new JLabel("Connecting...");
        labelStatus.setBounds(5, 65, 450, 20);
        add(labelStatus);

        setResizable(false);
    }

    public int askPort() {
        int port;

        while(true) {
            try {
                String portStr = JOptionPane.showInputDialog("What port does your server run on?");

                // This happens when the user selects "Cancel"
                if(portStr == null) {
                    System.exit(0);
                }

                port = Integer.parseInt(portStr);
                if(port < 1 || port > 65535) {
                    JOptionPane.showMessageDialog(null, "Wrong port! Choose one between 1 and 65535");
                } else {
                    return port;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Type a valid, integer port, please! (between 1 and 65535)");
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
