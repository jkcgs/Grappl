package com.daexsys.moxc.web;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class GrapplServer {

    public static HttpServer httpServer;

    public static void main(String[] args) {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(80), 0);
            httpServer.createContext("/", new GrapplHomepage());
            httpServer.createContext("/donate", new GrapplDonate());
//            httpServer.createContext("/search", new SearchHandler());
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
