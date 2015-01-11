package com.daexsys.moxc.portbuster;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static int highestPossible = 25566;
    public static void main(String[] args) {
        startServer();
    }

    public static void startServer() {
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
            final ServerSocket trafficSocket  = new ServerSocket(25563);
            final ServerSocket messageSocket  = new ServerSocket(25564);

            while(true) {
                System.out.println("Waiting for more connections");
                try {
                    final int port = highestPossible;
                    final Socket theListener = messageSocket.accept();

                    final ServerSocket externalSocket = new ServerSocket(highestPossible);
                    System.out.println("Waiting for connections at port: " + highestPossible);
                    highestPossible++;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    final Socket local = externalSocket.accept();

                                    System.out.println("Client connected");

                                    PrintStream printStream = new PrintStream(theListener.getOutputStream());
                                    printStream.println(port);

                                    // Get traffic socket.
                                    final Socket remote = trafficSocket.accept();
                                    ;

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                while (true) {
                                                    byte[] buffer = new byte[4096];
                                                    int size = 0;

                                                    while ((size = local.getInputStream().read(buffer)) != -1) {
                                                        remote.getOutputStream().write(buffer, 0, size);
                                                    }
                                                }
                                            } catch (Exception e) {
                                                try {
                                                    local.close();
                                                } catch (IOException e1) {
                                                }
                                            }
                                        }
                                    }).start();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                while (true) {
                                                    byte[] buffer = new byte[4096];
                                                    int size = 0;

                                                    while ((size = remote.getInputStream().read(buffer)) != -1) {
                                                        local.getOutputStream().write(buffer, 0, size);
                                                    }
                                                }
                                            } catch (Exception e) {
                                                try {
                                                    remote.close();
                                                } catch (IOException e1) {
                                                }
                                            }
                                        }
                                    }).start();
                                } catch (Exception e) {

                                }
                            }
                        }
                    }).start();
                } catch (Exception e) {

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
