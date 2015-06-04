package com.daexsys.moxc.portbuster;

import com.daexsys.moxc.GrapplGlobal;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Server {

    public static Set<Integer> portsTaken = new HashSet<Integer>();

    public static void main(String[] args) {
        startServer();
    }

    public static int getPort() {
        int choice = new Random().nextInt(40000) + 10000;

        if(!portsTaken.contains(choice)) {
            portsTaken.add(choice);
            return choice;
        }

        else return getPort();
    }

    public static void startServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);

                while(true) {
                    String line = scanner.nextLine();

                    String[] spl = line.split("\\s+");

                    if(spl[0].equalsIgnoreCase("quit")) {
                        System.exit(0);
                    }
                }
            }
        }).start();

        try {


        // THESE ARE ALWAYS OPEN
        final ServerSocket trafficSocket = new ServerSocket(GrapplGlobal.INNER_TRANSIT);
        final ServerSocket messageSocket = new ServerSocket(GrapplGlobal.SPAWN_PORT);

        // THIS SHOULD ALWAYS RUN
        System.out.println("GrapplServer started.");
        System.out.println("Waiting for connections.");

        boolean isRunning = true;

        // Waiting for connections
        while(isRunning) {
            // Accept a host connection.
            final Socket theListener = messageSocket.accept();
            System.out.println("HOST connected " + theListener.getInetAddress());

            final int SERVICE_PORT = getPort();

            // Tell the host what port they're running on.
            PrintStream printStream = new PrintStream(theListener.getOutputStream());
            printStream.println(SERVICE_PORT);

            final ServerSocket externalSocket = new ServerSocket(SERVICE_PORT);
            System.out.println("HOSTING connections at port: " + SERVICE_PORT);

            // Creating host ports
            Thread hostServer = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Start host code
                    try {
                        while (true) {
                            final Socket local = externalSocket.accept();

                            System.out.println(SERVICE_PORT + ": EX-CLIENT:(" + local.getInetAddress() + ") connected to HOST:(" + theListener.getInetAddress() + ")");

                            PrintStream printStream = new PrintStream(theListener.getOutputStream());
                            printStream.println(SERVICE_PORT);

                            // Get traffic socket.
                            final Socket remote = trafficSocket.accept();

                            Thread localToRemote = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    byte[] buffer = new byte[4096];
                                    int size;

                                    try {
                                        while ((size = local.getInputStream().read(buffer)) != -1) {
                                            remote.getOutputStream().write(buffer, 0, size);

                                            try {
                                                Thread.sleep(5);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (Exception e) {
                                        try {
                                            local.close();
                                            remote.close();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                    try {
                                        local.close();
                                        remote.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            localToRemote.start();

                            final Thread remoteToLocal = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    byte[] buffer = new byte[4096];
                                    int size;

                                    try {
                                        while ((size = remote.getInputStream().read(buffer)) != -1) {
                                            local.getOutputStream().write(buffer, 0, size);

                                            try {
                                                Thread.sleep(5);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (Exception e) {
                                        try {
                                            local.close();
                                            remote.close();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                    try {
                                        local.close();
                                        remote.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            remoteToLocal.start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("NO LONGER HOSTING connections at port: " + SERVICE_PORT);

                    try {
                        externalSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            hostServer.start();
        }



            // End search loop
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void log(String log) {
        System.out.println(System.currentTimeMillis() + ": " + log);

    }
}
