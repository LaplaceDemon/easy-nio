package io.github.laplacedemon.easynio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public class ChannelAcceptor {
    private ServerSocketChannel serverSocketChannel;
    private NIOEventLoop ioLoop;
    private int port;

    public void bind(int port) {
        this.port = port;
    }

    public void loopAccept(Consumer<Channel> channelConsumer) {
        ioLoop.register(serverSocketChannel, SelectionKey.OP_ACCEPT, sKey -> {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                Channel channel = new Channel(ioLoop);

                channelConsumer.accept(channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
