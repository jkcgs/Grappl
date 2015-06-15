package com.daexsys.grappl.server;

public class User {
    private int port = 40000;

    private String username;
    private String password;
    private String instanceId;

    public User(String username, String password, int port) {
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;

    }

    public boolean attemptLogin(String username, String password) {
        if(username.equalsIgnoreCase(username)) {
            if(password.equalsIgnoreCase(password)) {
                UserManager.loginUser(this);
                return true;
            }
        }

        return false;
    }

    public int getPort() {
        return port;
    }
}
