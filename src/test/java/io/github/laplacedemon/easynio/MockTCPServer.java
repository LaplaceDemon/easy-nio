package io.github.laplacedemon.easynio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MockTCPServer {
    private ServerSocket serverSocket;
    private int port;

    public MockTCPServer() {
    }

    public void bind(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Accept " + socket);
            new Thread(() -> {
                while(true) {
                    try {
                        byte[] bs = new byte[4096];
                        InputStream inputStream = socket.getInputStream();
                        int len = inputStream.read(bs);
                        if (len > 0) {
                            System.out.println("read bytes: " + len + new String(bs, 0, len));
                        } else {
                            System.out.println("read bytes: " + len);
                            if (len < 0) {
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }

                try {
                    socket.close();
                    System.out.println("Socket has closed. " + socket);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }

    public static void main(String[] args) throws IOException {
        MockTCPServer mockTCPServer = new MockTCPServer();
        mockTCPServer.bind(19000);
        System.out.println("MockSocket started");
        mockTCPServer.start();
    }
}
