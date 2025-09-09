package org.toop;

public class Server {

    String ip;
    String port;

    public Server(String set_ip, String set_port) {
        ip = set_ip;
        port = set_port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

}