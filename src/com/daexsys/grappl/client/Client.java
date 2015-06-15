package com.daexsys.grappl.client;

import com.daexsys.grappl.GrapplGlobal;

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
    public static volatile JLabel jLabel4;
    public static int sent = 0;
    public static int recv = 0;

    public static int connectedClients = 0;

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
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }

            jFrame = new JFrame(GrapplGlobal.APP_NAME + " Client");
            jFrame.setSize(new Dimension(300, 240));
            jFrame.setVisible(true);
            jFrame.setLayout(null);
            jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            JButton jButton = new JButton("Close " + GrapplGlobal.APP_NAME + " Client");
            jButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            jFrame.add(jButton);
            jButton.setBounds(0, 95, 280, 100);
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
            final Socket messageSocket = new Socket(ip, GrapplGlobal.MESSAGE_PORT);

            final DataInputStream messageInputStream = new DataInputStream(messageSocket.getInputStream());
            final String s = messageInputStream.readLine();
            System.out.println(GrapplGlobal.DOMAIN + ":" + s);

            if(displayGui) {
                final JLabel jLabel = new JLabel("Global Address: " + GrapplGlobal.DOMAIN + ":" + s);
                jLabel.setBounds(5, 5, 450, 20);
                jFrame.add(jLabel);

                JLabel jLabel2 = new JLabel("Server on local port: " + SERVICE_PORT);
                jLabel2.setBounds(5, 25, 450, 20);
                jFrame.add(jLabel2);

                jLabel4 = new JLabel("Waiting for data");
                jLabel4.setBounds(5, 65, 450, 20);
                jFrame.add(jLabel4);

                jFrame.repaint();
            }

            if(displayGui) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if (jLabel4 != null && jLabel3 != null) {
                                jLabel3.setText("Connected clients: " + connectedClients);
                                jLabel4.setText("Sent Data: " + (sent*4) + "KB - Recv Data: " + (recv*4)+"KB");
                                jFrame.repaint();
                            }

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Socket heartBeat = null;
                    DataOutputStream dataOutputStream = null;

                    try {
                        heartBeat = new Socket(GrapplGlobal.DOMAIN, GrapplGlobal.HEARTBEAT);
                        dataOutputStream = new DataOutputStream(heartBeat.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    while(true) {
                        try {
                            dataOutputStream.writeInt(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        if(displayGui) {
                            jLabel3 = new JLabel("Connected clients: " + connectedClients);
                            jLabel3.setBounds(5, 45, 450, 20);
                            jFrame.add(jLabel3);
                            jFrame.repaint();
                        }

                        while(true) {
                            // This goes off when a new client attempts to connect.
                            String a = messageInputStream.readLine();
                            System.out.println("A remote client has connected.");

                            // Increment the connected player counter.
                            connectedClients++;

                            // This socket connects to the local server.
                            final Socket toLocal = new Socket("127.0.0.1", SERVICE_PORT);
                            // This socket connects to the grappl server, to transfer data from the computer to it.
                            final Socket toRemote = new Socket(GrapplGlobal.DOMAIN, Integer.parseInt(s) + 1);

                            // Start the local -> remote thread
                            final Thread localToRemote = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    byte[] buffer = new byte[4096];
                                    int size;

                                    try {
                                        while ((size = toLocal.getInputStream().read(buffer)) != -1) {
                                            toRemote.getOutputStream().write(buffer, 0, size);
                                            sent += 1;
                                        }
                                    } catch (IOException e) {
                                        try {
                                            toLocal.close();
                                            toRemote.close();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
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
                                            recv += 1;
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
                                        e.printStackTrace();
                                    }
                                }
                            });
                            remoteToLocal.start();
                        }
                    } catch (IOException e) {
                        try {
                            messageSocket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
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
