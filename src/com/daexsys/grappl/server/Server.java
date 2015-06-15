package com.daexsys.grappl.server;

import com.daexsys.grappl.GrapplGlobal;
import com.daexsys.grappl.web.WebServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.*;

public class Server {

    public static ServerSocket trafficServer;
    public static ServerSocket messageServer;
    public static ServerSocket heartBeatServer;

    public static List<Host> hosts = new ArrayList<Host>();

    public static Map<String, Long> heartBeats = new HashMap<String, Long>();
    public static Map<String, Host> hostMap = new HashMap<String, Host>();
    public static Map<Integer, Host> portMap = new HashMap<Integer, Host>();

    public static Set<Integer> portsTaken = new HashSet<Integer>();

    public static void main(String[] args) {
        startServer();
    }

    public static int port = 40000;

    public static int getPort(User user) {
        port += 2;
        return port;
//        int choice = new Random().nextInt(40000) + 10000;
//
//        if (!portsTaken.contains(choice)) {
//            portsTaken.add(choice);
//            return choice;
//        } else return getPort(user);
    }

    static {
        try {
            trafficServer = new ServerSocket(GrapplGlobal.INNER_TRANSIT);
            messageServer = new ServerSocket(GrapplGlobal.MESSAGE_PORT);
            heartBeatServer = new ServerSocket(GrapplGlobal.HEARTBEAT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startServer() {

        // Start the web server
        WebServer.main(null);

        log("GrapplServer started.");
        log("Waiting for connections.");

        final Thread heartbeatReception = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        final Socket heartBeatClient = heartBeatServer.accept();

                        final String server = heartBeatClient.getInetAddress().toString();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    DataInputStream dataInputStream = new DataInputStream(heartBeatClient
                                                .getInputStream());
                                    while(true) {
                                        int time = dataInputStream.readInt();

                                        tickHost(server);

                                        try {
                                            Thread.sleep(50);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (IOException e) {
                                    hostMap.get(server).closeHost();
                                }
                            }
                        }).start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        heartbeatReception.start();

        Thread commandThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);

                while(true) {
                    String line = scanner.nextLine();

                    String[] spl = line.split("\\s+");

                    if(spl[0].equalsIgnoreCase("quit")) {
                        System.exit(0);
                    }

                    else if (spl[0].equalsIgnoreCase("hosts")) {
                       log(hosts.size() + "");
                    }

                    else if(spl[0].equalsIgnoreCase("hostlist")) {
                        String output = hosts.size() + " host(s): ";

                        for (int i = 0; i < hosts.size(); i++) {
                            if(i != 0) {
                                output += " - ";
                            }
                            Host host = hosts.get(i);

                            output += host.getAddress() + ":" + host.getPortNumber();
                        }

                        log(output);
                    }

                    else if(spl[0].equalsIgnoreCase("debind")) {
                        try {
                            getHost(Integer.parseInt(spl[1])).closeHost();
                        } catch (Exception er) {
                            log("An error occurred.");
                        }
                    }

                    else {
                        log("Unknown command!");
                    }
//                    else if(spl[0].equalsIgnoreCase("refresh")) {
//                        System.out.println("[CONSOLE] Attempting refresh");
//                    }
                }
            }
        });
        commandThread.start();

        boolean isRunning = true;
        // Waiting for connections from hosts
        while(isRunning) {
            // Accept a host connection.
            try {
                final Socket hostSocket = messageServer.accept();

                // Getting of user login information will occur here

                Host host = new Host(hostSocket, null);
                host.start();
                addHost(host);
            } catch (Exception e) {
                e.printStackTrace();
                isRunning = false;
            }
        }

        log("Fatal error. Closing.");
    }

    public static void addHost(Host host) {
        hosts.add(host);
        hostMap.put(host.getAddress(), host);
        portMap.put(host.getPortNumber(), host);
        heartBeats.put(host.getAddress(), System.currentTimeMillis());
    }

    public static void removeHost(Host host) {
        hosts.remove(host);
        portsTaken.remove(host.getPortNumber());
    }

    public static Host getHost(int port) {
        return portMap.get(port);
    }

    public static void tickHost(String ip) {
        heartBeats.put(ip, System.currentTimeMillis());
    }

    public static long getHostTick(String ip) {
        if(heartBeats.containsKey(ip)) {
            return heartBeats.get(ip);
        } else return System.currentTimeMillis();
    }

    public static int connectedHosts() {
        return hosts.size();
    }

    public static void log(String log) {
        String tag = DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
        System.out.println("[" + tag + "] " + log);
    }
}
