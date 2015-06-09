package com.daexsys.grappl.web;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Used to handle the web frontend for Grappl.
 */
public class WebServer {

    public static HttpServer httpServer;

    public static void main(String[] args) {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
            httpServer.createContext("/", new GrapplHomepage());
            httpServer.createContext("/donate", new GrapplDonate());
            httpServer.createContext("/stats", new GrapplStats());
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
