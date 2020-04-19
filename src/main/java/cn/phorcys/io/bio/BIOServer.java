package cn.phorcys.io.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class BIOServer {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket();
            ss.bind(new InetSocketAddress("127.0.0.1", 8000));
            System.out.println("Socket Server has been established now at 127.0.0.1:8000");
            while (true) {
                Socket accept = ss.accept();

                new Thread(() -> {
                    handle(accept);
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handle(Socket socket) {
        byte[] bytes = new byte[1024];
        try {
            int len = socket.getInputStream().read(bytes);
            socket.getOutputStream().write(bytes, 0, len);
            socket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
