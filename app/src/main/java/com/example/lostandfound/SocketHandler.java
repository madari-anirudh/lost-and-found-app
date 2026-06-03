package com.example.lostandfound;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketHandler {

    private static Socket socket;

    public static void setSocket() {
        try {
            //  IMPORTANT: Use your PC IP (NOT localhost)
            socket = IO.socket("http://192.168.137.1:5000");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void establishConnection() {
        socket.connect();
    }

    public static void closeConnection() {
        socket.disconnect();
    }
}