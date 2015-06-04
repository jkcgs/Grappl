package com.daexsys.moxc.portbuster;

import com.daexsys.moxc.GrapplGlobal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static JFrame jFrame;
    public static JLabel jLabel3;

    public static void main(String[] args) {
        boolean displayGui = true;
        int port = 25566;

        if(args.length > 1) {
            if (args[0].equalsIgnoreCase("nogui")) {
                displayGui = false;
            }

            port = Integer.parseInt(args[1]);
        }

        proceed(GrapplGlobal.DOMAIN, port, displayGui);
    }

    public static void proceed(final String ip, int port, final boolean displayGui) {
        if(displayGui) {
            jFrame = new JFrame(GrapplGlobal.APP_NAME + " Client");
            jFrame.setSize(new Dimension(300, 220));
            jFrame.setVisible(true);
            jFrame.setLayout(null);

            JButton jButton = new JButton("Close " + GrapplGlobal.APP_NAME + " Client");
            jButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            jFrame.add(jButton);
            jButton.setBounds(0, 75, 280, 100);
        }

        if(displayGui) {
            String ports = JOptionPane.showInputDialog("What port does your server run on?");
            port = Integer.parseInt(ports);
        }

        final int SERVICE_PORT = port;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);

                while(true) {
                    String line = scanner.nextLine();

                    String[] spl = line.split("\\s+");

                    if(line.equalsIgnoreCase("quit")) {
                        System.exit(0);
                    }
                }
            }
        }).start();

        try {
            // Create socket listener
            final Socket messageSocket = new Socket(ip, GrapplGlobal.SPAWN_PORT);
            new PrintStream(messageSocket.getOutputStream()).println(SERVICE_PORT + "");

            final DataInputStream messageInputStream = new DataInputStream(messageSocket.getInputStream());
            String s = messageInputStream.readLine();
            System.out.println(GrapplGlobal.DOMAIN + ":" + s);

            if(displayGui) {
                final JLabel jLabel = new JLabel("GLOBAL ADDRESS: " + GrapplGlobal.DOMAIN + ":" + s);
                jLabel.setBounds(5, 5, 450, 20);
                jFrame.add(jLabel);

                JLabel jLabel2 = new JLabel("SERVER ON LOCAL PORT: " + SERVICE_PORT);
                jLabel2.setBounds(5, 25, 450, 20);
                jFrame.add(jLabel2);
                jFrame.repaint();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int connectedClients = 0;

                        if(displayGui) {
                            jLabel3 = new JLabel("CONNECTED CLIENTS: " + connectedClients);
                            jLabel3.setBounds(5, 45, 450, 20);
                            jFrame.add(jLabel3);
                            jFrame.repaint();
                        }

                        while(true) {
                            // This goes off when a new client attempts to connect.
                            String s = messageInputStream.readLine();
                            System.out.println("A remote client has connected.");

                            // Increment the connected player counter.
                            connectedClients++;
                            if(displayGui) {
                                jLabel3.setText("CONNECTED CLIENTS: " + connectedClients);
                                jFrame.repaint();
                            }

                            // This socket connects to the local server.
                            final Socket toLocal = new Socket("127.0.0.1", SERVICE_PORT);
                            // This socket connects to the grappl server, to transfer data from the computer to it.
                            final Socket toRemote = new Socket(GrapplGlobal.DOMAIN, GrapplGlobal.INNER_TRANSIT);

                            // Start the local -> remote thread
                            final Thread localToRemote = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    byte[] buffer = new byte[4096];
                                    int size;

                                    try {
                                        while ((size = toLocal.getInputStream().read(buffer)) != -1) {
                                            toRemote.getOutputStream().write(buffer, 0, size);
                                        }
                                    } catch (IOException e) {
                                        try {
                                            toLocal.close();
                                            toRemote.close();
                                        } catch (IOException e1) {
//                                            e1.printStackTrace();
                                        }
//                                        e.printStackTrace();
                                    }

                                    try {
                                        toLocal.close();
                                        toRemote.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            localToRemote.start();

//                            Start the remote -> local thread
                            final Thread remoteToLocal = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    byte[] buffer = new byte[4096];
                                    int size;

                                    try {
                                        while ((size = toRemote.getInputStream().read(buffer)) != -1) {
                                            toLocal.getOutputStream().write(buffer, 0, size);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        try {
                                            toLocal.close();
                                            toRemote.close();
                                        } catch (IOException e1) {
//                                            e1.printStackTrace();
                                        }
                                    }

                                    try {
                                        toLocal.close();
                                        toRemote.close();
                                    } catch (IOException e) {
//                                        e.printStackTrace();
                                    }
                                }
                            });
                            remoteToLocal.start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateConnections() {

    }
}
