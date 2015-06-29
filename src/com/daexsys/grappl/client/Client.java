package com.daexsys.grappl.client;

import com.daexsys.grappl.GrapplGlobal;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private ClientGui clientGUI;
    private LoginGui loginGUI;
    private int sent = 0;
    private int recv = 0;
    private int servicePort = 0;
    private int relayedPort = 0;

    private String serviceDomain = GrapplGlobal.DOMAIN;
    private String username = "Anonymous";
    private boolean isAlphaTester = false;
    private boolean isLoggedIn = false;

    private int connectedClients = 0;

    public static void main(String[] args) {
        int port = 25566;
        boolean displayGui = true;
        Client client = new Client();

        if(args.length > 1) {
            if (args[0].equalsIgnoreCase("nogui")) {
                displayGui = false;
                try {
                    port = Integer.parseInt(args[1]);
                    if(port < 1 || port > 65535) {
                        throw new NumberFormatException();
                    } else {
                        client.setServicePort(port);
                    }
                } catch(NumberFormatException e) {
                    System.out.println("Wrong servicePort number! You must enter a valid server servicePort number between 1 and 65535");
                    System.out.println("Example: java -jar GrappleClient.jar nogui 7777");
                    System.exit(0);
                }
            }
        }

        if(displayGui) {
            client.guiInit();
        }

        client.proceed();
    }

    public void guiInit() {
        loginGUI = new LoginGui();
        clientGUI = new ClientGui();

        loginGUI.buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = loginGUI.inputUser.getText();
                String ipass = new String(loginGUI.inputPassword.getPassword());

                if(username.isEmpty()) {
                    JOptionPane.showMessageDialog(loginGUI, "Please type the username!");
                    return;
                }

                try {
                    relayedPort = login(username, ipass);

                    if (isLoggedIn) {
                        System.out.println("Logged in as " + username);
                        System.out.println("Alpha tester: " + isAlphaTester);
                        System.out.println("Static port: " + relayedPort);

                        System.out.println(serviceDomain);

                        int wX = clientGUI.getX();
                        int wY = clientGUI.getY();
                        loginGUI.setVisible(false);

                        clientGUI.setLocation(wX, wY);
                        clientGUI.setVisible(true);

                        setServicePort(clientGUI.askPort());
                        init();
                    } else {
                        JOptionPane.showMessageDialog(loginGUI, "Login failed!");
                    }
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            }
        });

        //202
        loginGUI.buttonAnonymous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginGUI.setVisible(false);
                int wX = loginGUI.getX();
                int wY = loginGUI.getY();

                clientGUI.setLocation(wX, wY);
                clientGUI.setVisible(true);

                setServicePort(clientGUI.askPort());
                init();
            }
        });

        loginGUI.setVisible(true);
    }

    public void proceed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream dataOutputStream = null;

                try {
                    Socket socket = new Socket(serviceDomain, GrapplGlobal.AUTHENTICATION);
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                } catch (Exception e) {
                    if(clientGUI != null) {
                        JOptionPane.showMessageDialog(clientGUI, "Error detected trying to connect to the server! Closing...");
                    }

                    e.printStackTrace();
                    System.exit(0);
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

                            int port = login(username, password);

                            if(isLoggedIn) {
                                System.out.println("Logged in as " + username);
                                System.out.println("Alpha tester: " + isAlphaTester);
                                System.out.println("Static servicePort: " + port);
                            } else {
                                System.out.println("Login failed!");
                            }
                        }

                        else if(command.equals("whoami")) {
                            // If not logged in, it prints Anonymous anyway (defined on class variable).
                            System.out.println(username);
                        }

                        else if(command.equals("setport")) {
                            if(isLoggedIn) {
                                if (isAlphaTester) {
                                    dataOutputStream.writeByte(2);
                                    dataOutputStream.writeInt(Integer.parseInt(spl[1]));
                                    System.out.println("Your servicePort was set to: " + Integer.parseInt(spl[1]));
                                } else {
                                    System.out.println("You are not an alpha tester, so you can't set static ports.");
                                }
                            } else {
                                System.out.println("You are not logged in.");
                            }
                        }

                        else if (command.equals("init")) {
                            System.out.println("Starting...");
                            init();
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

    public void init() {
        final int SERVICE_PORT = servicePort;

//        if(ip.substring(0, 1).equalsIgnoreCase(".")) { ip = ip.substring(1, ip.length()); }
        try {

            // Create socket listener
            final Socket messageSocket = new Socket(serviceDomain, GrapplGlobal.MESSAGE_PORT);

            final BufferedReader messageInputStream = new BufferedReader(new InputStreamReader(messageSocket.getInputStream()));
            final String s = messageInputStream.readLine();
            System.out.println(serviceDomain + ":" + s);

            if(clientGUI != null) {
                clientGUI.labelAddress.setText("Global Address: " + GrapplGlobal.DOMAIN + ":" + s);
                clientGUI.labelPort.setText("Server on local port: " + SERVICE_PORT);
                clientGUI.labelStatus.setText("Waiting for data");

                clientGUI.repaint();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            clientGUI.labelClients.setText("Connected clients: " + connectedClients);
                            clientGUI.labelStatus.setText("Sent Data: " + (sent*4) + "KB - Recv Data: " + (recv*4)+"KB");
                            clientGUI.repaint();

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
                        heartBeat = new Socket(serviceDomain, GrapplGlobal.HEARTBEAT);
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
                        if(clientGUI != null) {
                            clientGUI.labelClients.setText("Connected clients: " + connectedClients);
                            clientGUI.repaint();
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
                            System.out.println(serviceDomain);
                            final Socket toRemote = new Socket(serviceDomain, Integer.parseInt(s) + 1);

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

    /**
     * Login to the system.
     * @param user The session user
     * @param pass The session password
     * @return On successful login it returns the servicePort relayed to on remote system. Else, returns 0.
     * @throws IOException
     */
    public int login(String user, String pass) throws IOException {
        DataOutputStream outputStream;
        DataInputStream inputStream;

        Socket socket = new Socket(GrapplGlobal.DOMAIN, GrapplGlobal.AUTHENTICATION);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());

        outputStream.writeByte(0);

        PrintStream printStream = new PrintStream(outputStream);
        printStream.println(user);
        printStream.println(pass);

        isLoggedIn = inputStream.readBoolean();
        isAlphaTester = inputStream.readBoolean();

        int staticPort = inputStream.readInt();

        if(isLoggedIn) {
            // options: nyc. sf. pac. lon. deu.
            username = user;
            String prefix = inputStream.readLine();
            serviceDomain = prefix + "." + GrapplGlobal.DOMAIN;
            return staticPort;
        } else {
            return 0;
        }
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }
}
