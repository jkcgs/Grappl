package com.daexsys.moxc.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class GrapplHomepage implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        StringBuilder response = new StringBuilder();

        response.append("<html>");
        response.append("<link href='http://fonts.googleapis.com/css?family=Varela+Round' rel='stylesheet' type='text/css'>");
        response.append("<title>grappl - you are the cloud now</title>");

        response.append("<body bgcolor = '292F54' link = 'white'>");
        response.append("<center>");

        response.append("<font color = 'E5E9FF' face ='Velera Round'>");

        response.append("<font size = '6'>");
        response.append("grappl<br></font>");
        response.append("<font size = '5'>");
        response.append("you are the cloud now<p>");

        response.append("<p>");

        response.append("<font size = '3'>");
        response.append("grappl is a clever little tool that let's you host any type of server anywhere, with no port forwarding and no vps<br>");
        response.append("your router doesn't even have to support upnp. you don't even have to have a router. you can do it on your phone<br>");
        response.append("and it's so simple to set up! no more hassling with routers<p>");

        response.append("<b>instructions</b><br>");
        response.append("just run it, and enter the port your server runs on<br>");
        response.append("the public address will appear in the grappl window");
        response.append("</font>");

        response.append("<p>");
        response.append("<a href = 'https://dl.dropboxusercontent.com/u/34769058/grappl/GrapplClient.jar'>download grappl and get hosting</a><p>");
        response.append("<a href = '/donate'>donate to help grappl keep hosting this free and simple</a><p>");

        response.append("<a href = 'https://twitter.com/Cactose'>dev contact</a>");

        httpExchange.sendResponseHeaders(200, response.length());
        httpExchange.getResponseBody().write(response.toString().getBytes());
        httpExchange.getResponseBody().close();
    }
}
