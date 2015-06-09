package com.daexsys.grappl.server;

import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;

public class ExClient {
    private Host host;

    public ExClient(Host host, Socket local) {
        this.host = host;
    }

//    public void start() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    // Get traffic socket.
//                    final Socket remote = Server.trafficServer.accept();
//
//                    log(SERVICE_PORT + ": EX-CLIENT:(" + local.getInetAddress() + ") has connected to HOST:(" +
//                            messageSocket.getInetAddress() + ")");
//
//                    Thread localToRemote = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            byte[] buffer = new byte[4096];
//                            int size;
//
//                            try {
//                                while ((size = local.getInputStream().read(buffer)) != -1) {
//                                    remote.getOutputStream().write(buffer, 0, size);
//
//                                    try {
//                                        Thread.sleep(5);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            } catch (Exception e) {
//                                try {
//                                    local.close();
//                                    remote.close();
//                                    clientCount--;
//                                } catch (IOException e1) {
//                                    e1.printStackTrace();
//                                }
//                            }
//
//                            try {
//                                local.close();
//                                remote.close();
//                                clientCount--;
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                    localToRemote.start();
//
//                    final Thread remoteToLocal = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            byte[] buffer = new byte[4096];
//                            int size;
//
//                            try {
//                                while ((size = remote.getInputStream().read(buffer)) != -1) {
//                                    local.getOutputStream().write(buffer, 0, size);
//
//                                    try {
//                                        Thread.sleep(5);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            } catch (Exception e) {
//                                try {
//                                    local.close();
//                                    remote.close();
//                                    clientCount--;
//                                } catch (IOException e1) {
//                                    e1.printStackTrace();
//                                }
//                            }
//
//                            try {
//                                local.close();
//                                remote.close();
//                                clientCount--;
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                    remoteToLocal.start();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

    public static void log(String log) {
        String tag = DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
        System.out.println("[" + tag + "] " + log);
    }

    public Host getHost() {
        return host;
    }
}
