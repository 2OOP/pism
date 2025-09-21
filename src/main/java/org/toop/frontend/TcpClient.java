package org.toop.frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/** A simple wrapper for creating TCP clients. */
public abstract class TcpClient {

    InetAddress serverAddress;
    int serverPort;
    Socket socket;
    BufferedReader in;
    PrintWriter out;

    public TcpClient(byte[] serverIp, int serverPort) throws IOException {
        this.serverAddress = InetAddress.getByAddress(serverIp);
        this.serverPort = serverPort;
        this.socket = createSocket();
        this.in = createIn();
        this.out = createOut();
    }

    public TcpClient(String serverIp, int serverPort) throws IOException {
        this.serverAddress = InetAddress.getByName(serverIp);
        this.serverPort = serverPort;
        this.socket = createSocket();
        this.in = createIn();
        this.out = createOut();
    }

    public Socket createSocket() throws IOException {
        return new Socket(serverAddress, serverPort);
    }

    public void closeSocket() throws IOException {
        this.socket.close();
    }

    BufferedReader createIn() throws IOException {
        return new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    PrintWriter createOut() throws IOException {
        return new PrintWriter(this.socket.getOutputStream(), true);
    }

    public void sendMessage(String message) throws IOException {
        this.out.println(message);
    }

    public String readLine() throws IOException {
        return this.in.readLine();
    }

    public void close() throws IOException {
        this.socket.close();
    }
}
