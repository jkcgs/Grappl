package com.daexsys.grappl.server;

import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.Map;

public class UserManager {

    private static Map<String, User> users = new HashMap<String, User>();
    private static Map<String, User> ids = new HashMap<String, User>();

    static {
        // load
        addUser("cactose", new User("cactose", "hashbrown", 31337));
    }

    public static void loginUser(User user) {
        user.setInstanceId(System.currentTimeMillis() + "");
    }

    private static void addUser(String name, User user) {
        users.put(name, user);
    }

    public static User getUserByName(String name) {
        return users.get(name);
    }

    public static User getUserById(String id) {
        return ids.get(id);
    }

    public static User getUser(HttpExchange httpExchange) {
        String cookie = httpExchange.getRequestHeaders().getFirst("Cookie");

        Map<String, String> cookieParams = new HashMap<String, String>();

        try {
            if(!cookie.equalsIgnoreCase("")) {

                String[] cookieTag = cookie.split("\\;");

                for (String string : cookieTag) {
                    String[] pair = string.split("\\=");

                    String key = pair[0].replaceAll(" ", "");
                    String value = pair[1].replaceAll(" ", "");

                    cookieParams.put(key, value);
                }
            }else {
                System.out.println("empty cookie");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getUserByName(cookieParams.get("id"));
    }

    public static boolean personExists(String name) {
        return users.containsKey(name);
    }
}
