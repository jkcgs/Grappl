package portbuster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        startServer("play.daexsys.com");
    }

    public static void startServer(final String ip) {
        JFrame jFrame = new JFrame("MoxC Client");
        jFrame.setSize(new Dimension(300, 100));
        jFrame.setVisible(true);

        JButton jButton = new JButton("Close MoxC Client");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        jFrame.add(jButton);

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
            final Socket socket = new Socket(ip, 25564);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                        while(true) {
                            String s = dataInputStream.readLine();
//                            System.out.println("Client connected: play.daexsys.com:" + s);

                            // Per client socket
                            final Socket local = new Socket(ip, 25563);
                            final Socket remote = new Socket("127.0.0.1", 25565);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
//                                    while(true) {
                                        byte[] buffer = new byte[4096];
                                        int size = 0;

                                        try {
                                            while ((size = local.getInputStream().read(buffer)) != -1) {
                                                remote.getOutputStream().write(buffer, 0, size);
                                            }
                                        } catch (IOException e) {
                                            try {
                                                Thread.sleep(100000000);
                                            } catch (InterruptedException e1) {
                                                e1.printStackTrace();
                                            }
                                            try {
                                                local.close();
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                            e.printStackTrace();
                                        }
//                                    }
                                }
                            }).start();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
//                                    while(true) {
                                        byte[] buffer = new byte[4096];
                                        int size = 0;

                                        try {
                                            while ((size = remote.getInputStream().read(buffer)) != -1) {
                                                local.getOutputStream().write(buffer, 0, size);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            try {
                                                Thread.sleep(100000000);
                                            } catch (InterruptedException e1) {
                                                e1.printStackTrace();
                                            }
                                            try {
                                                remote.close();
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
//                                        }
                                    }
                                }
                            }).start();
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
}
