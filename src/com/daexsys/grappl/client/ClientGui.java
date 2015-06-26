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
        setVisible(true);
        setLayout(null);
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
        labelAddress = new JLabel();
        labelAddress.setBounds(5, 5, 450, 20);
        add(labelAddress);

        labelPort = new JLabel();
        labelPort.setBounds(5, 25, 450, 20);
        add(labelPort);

        labelStatus = new JLabel();
        labelStatus.setBounds(5, 65, 450, 20);
        add(labelStatus);

        labelClients = new JLabel();
        labelClients.setBounds(5, 45, 450, 20);
        add(labelClients);
    }

    public int askPort() {
        int port = 0;
        boolean ok = false;
        while(!ok) {
            try {
                String portStr = JOptionPane.showInputDialog("What port does your server run on?");
                port = Integer.parseInt(portStr);

                if(port < 1 || port > 65535) {
                    JOptionPane.showMessageDialog(null, "Wrong port! Choose one between 1 and 65535");
                } else {
                    ok = true;
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Type a valid port, please!");
            }
        }

        return port;
    }
}
