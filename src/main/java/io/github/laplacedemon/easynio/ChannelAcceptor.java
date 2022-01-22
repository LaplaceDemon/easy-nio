package io.github.laplacedemon.easynio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public class ChannelAcceptor {
    private ServerSocketChannel serverSocketChannel;
    private NIOEventLoop ioLoop;

    public ChannelAcceptor(NIOEventLoop ioLoop) {
        this.ioLoop = ioLoop;
    }

    void open() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
    }

    public void bind(SocketAddress local) throws IOException {
        serverSocketChannel.bind(local);
    }

    public void loopAccept(Consumer<Channel> channelConsumer) {
        ioLoop.register(serverSocketChannel, SelectionKey.OP_ACCEPT, sKey -> {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                Channel channel = new Channel(ioLoop);
                channel.open(socketChannel);
                channelConsumer.accept(channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
