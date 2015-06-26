package com.daexsys.grappl.client;

import com.daexsys.grappl.GrapplGlobal;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private ClientGui gui;
    private int sent = 0;
    private int recv = 0;

    private String username = "Anonymous";
    private boolean isAlphaTester = false;
    private boolean isLoggedIn = false;

    private int connectedClients = 0;

    public static void main(String[] args) {
        int port = 25566;
        boolean displayGui = true;

        if(args.length > 1) {
            if (args[0].equalsIgnoreCase("nogui")) {
                displayGui = false;
                try {
                    port = Integer.parseInt(args[1]);
                    if(port < 1 || port > 65535) {
                        throw new NumberFormatException();
                    }
                } catch(NumberFormatException e) {
                    System.out.println("Wrong port number! You must enter a valid server port number between 1 and 65535");
                    System.out.println("Example: java -jar GrappleClient.jar nogui 7777");
                    System.exit(0);
                }
            }
        }

        (new Client()).proceed(GrapplGlobal.DOMAIN, port, displayGui);
    }

    public void proceed(final String ip, int port, boolean displayGui) {
        if(displayGui) {
            gui = new ClientGui();

            port = gui.askPort();
            init(ip, port);
        }

        final int SERVICE_PORT = port;

        new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream dataInputStream = null;
                DataOutputStream dataOutputStream = null;

                try {
                    Socket socket = new Socket(GrapplGlobal.DOMAIN, GrapplGlobal.AUTHENTICATION);
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("Grappl command line");
                System.out.println("--------------\n\n");

                Scanner scanner = new Scanner(System.in);

                while(true) {
                    try {
                        System.out.print("> ");
                        String line = scanner.nextLine();

                        String[] spl = line.split("\\s+");
                        String command = spl[0].toLowerCase();

                        if (command.equals("ipban")) {
                            if(isLoggedIn) {
                                String ip = spl[1];

                                dataOutputStream.writeByte(5);
                                PrintStream printStream = new PrintStream(dataOutputStream);
                                printStream.println(ip);

                                System.out.println("Banned ip: " + ip);
                            } else {
                                System.out.println("You must be logged in to ban IPs.");
                            }
                        }

                        else if (command.equals("login")) {
                            String username = spl[1];
                            String password = spl[2];

                            dataOutputStream.writeByte(0);

                            PrintStream printStream = new PrintStream(dataOutputStream);
                            printStream.println(username);
                            printStream.println(password);

                            isLoggedIn = dataInputStream.readBoolean();
                            isAlphaTester = dataInputStream.readBoolean();
                            int port = dataInputStream.readInt();

                            if(isLoggedIn) {
                                System.out.println("Logged in as " + username);
                                System.out.println("Alpha tester: " + isAlphaTester);
                                System.out.println("Static port: " + port);
                            } else {
                                System.out.println("Login failed!");
                            }
                        }

                        else if(command.equals("whoami")) {
                            if(isLoggedIn) {
                                System.out.println(username);
                            } else {
                                System.out.println("You aren't logged in, so you are anonymous.");
                            }
                        }

                        else if(command.equals("setport")) {
                            if(isLoggedIn) {
                                if (isAlphaTester) {
                                    dataOutputStream.writeByte(2);
                                    dataOutputStream.writeInt(Integer.parseInt(spl[1]));
                                    System.out.println("Your port was set to: " + Integer.parseInt(spl[1]));
                                } else {
                                    System.out.println("You are not an alpha tester, so you can't set static ports.");
                                }
                            } else {
                                System.out.println("You are not logged in.");
                            }
                        }

                        else if (command.equals("init")) {
                            System.out.println("Starting...");
                            init(ip, SERVICE_PORT);
                        }

                        else if (line.equalsIgnoreCase("quit")) {
                            System.exit(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void init(String ip, int port) {
            final int SERVICE_PORT = port;

        try {
            // Create socket listener
            final Socket messageSocket = new Socket(ip, GrapplGlobal.MESSAGE_PORT);

            final BufferedReader messageInputStream = new BufferedReader(new InputStreamReader(messageSocket.getInputStream()));
            final String s = messageInputStream.readLine();
            System.out.println(GrapplGlobal.DOMAIN + ":" + s);

            if(gui != null) {
                gui.labelAddress.setText("Global Address: " + GrapplGlobal.DOMAIN + ":" + s);
                gui.labelPort.setText("Server on local port: " + SERVICE_PORT);
                gui.labelStatus.setText("Waiting for data");

                gui.repaint();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            gui.labelClients.setText("Connected clients: " + connectedClients);
                            gui.labelStatus.setText("Sent Data: " + (sent*4) + "KB - Recv Data: " + (recv*4)+"KB");
                            gui.repaint();

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
                        if(gui != null) {
                            gui.labelClients.setText("Connected clients: " + connectedClients);
                            gui.repaint();
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

                            // Start the remote -> local thread
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
