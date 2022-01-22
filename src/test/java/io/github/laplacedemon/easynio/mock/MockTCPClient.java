package io.github.laplacedemon.easynio.mock;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

public class MockTCPClient {
    private Socket socket;

    public MockTCPClient() {
        socket = new Socket();
    }

    public void connect(SocketAddress endpoint) throws IOException {
        socket.connect(endpoint);
    }

}
