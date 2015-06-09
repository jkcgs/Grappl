package com.daexsys.grappl.web;

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

        response.append("<body bgcolor = '85A5DB' link = 'white'>");
        response.append("<center>");

        response.append("<div align='center' style='background-color:7283A8; width: 800px;'>");
        response.append("<div align = 'center' style='background-color:5A73AA;'");
        response.append("<font color = 'E5E9FF' face ='Velera Round'>");
        response.append("<font size = '6' color = 'FFFFFF'>");
        response.append("<img src = 'https://dl.dropboxusercontent.com/u/34769058/grappl/logo.png'><br>grappl<br></font>");
        response.append("<font size = '5' color = 'E5E9FF'>");
        response.append("you are the cloud now<p>");
        response.append("</div>");

        response.append("<p>");

        response.append("<font size = '3'>");
        response.append("<b>grappl</b> is a clever little tool that lets you host any type of server anywhere, with no port forwarding and no VPS.<br>");
        response.append("Your router doesn't even have to support uPnP. You don't even have to have a router. You can do it on your phone!<p>");
//        response.append("It's so simple to set up!<p>");

        response.append("<img src = 'https://dl.dropboxusercontent.com/u/34769058/grapplshot.PNG'><p>");

        response.append("<b>Instructions</b><br>");
        response.append("Just run it, and enter the port your server runs on.<br>");
        response.append("The public address will appear in the grappl window.");
        response.append("</font>");

        response.append("<p>");
        response.append("<div style='background-color:5A73AA;'>");
        response.append("<a href = 'https://dl.dropboxusercontent.com/u/34769058/grappl/GrapplClient.jar'>Download grappl and get hosting!</a><p>");
        response.append("<a href = '/donate'>Donate to help grappl keep hosting this free and simple</a><p>");

        response.append("<a href = 'https://twitter.com/Cactose'>Dev twitter</a> -");
        response.append("<a href = 'https://github.com/Cactose/Grappl'>The source</a><p>");

        httpExchange.sendResponseHeaders(200, response.length());
        httpExchange.getResponseBody().write(response.toString().getBytes());
        httpExchange.getResponseBody().close();
    }
}
