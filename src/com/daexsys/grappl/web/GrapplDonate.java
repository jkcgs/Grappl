package com.daexsys.grappl.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class GrapplDonate implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        StringBuilder response = new StringBuilder("");
        response.append("<html>");
        response.append("<title>grappl - you are the cloud now</title>");

        response.append("<body bgcolor = '292F54'>");
        response.append("<center>");
        response.append("<font size = '6' color = 'ffffff'>");
        response.append("you can donate any amount you want<p>");
//        response.append("<font size = '4'>");
//        response.append("grappl costs $10 a month to host<br>extra funds allow for expansion!");
//        response.append("which means more bandwidth and improvem!");

        String donateButton = "<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\" target=\"_top\">\n" +

                "<input type=\"hidden\" name=\"cmd\" value=\"_s-xclick\">\n" +
                "<input type=\"hidden\" name=\"encrypted\" value=\"-----BEGIN " +
                "PKCS7-----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-----END PKCS7-----\n" +
                "\">\n" +
                "<input type=\"image\" src=\"https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif\" border=\"0" +
                "\" " +
                "name=\"submit\" alt=\"PayPal - The safer, easier way to pay online!\">\n" +
                "<img alt=\"\" border=\"0\" src=\"https://www.paypalobjects.com/en_US/i/scr/pixel.gif\" width=\"1\" " +
                "height=\"1\">\n" +
                "</form>\n";

        response.append(donateButton);

        httpExchange.sendResponseHeaders(200, response.length());
        httpExchange.getResponseBody().write(response.toString().getBytes());
        httpExchange.getResponseBody().close();
    }
}
