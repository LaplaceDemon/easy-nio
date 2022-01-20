package io.github.laplacedemon.easynio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public class Channel {
    private SocketChannel socketChannel;
    private NIOEventLoop ioLoop;

    public SocketChannel javaChannel() {
        return socketChannel;
    }

    public Channel(NIOEventLoop ioLoop) {
        this.ioLoop = ioLoop;
    }

    public void open() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
    }

    public void connect(InetSocketAddress address, Runnable runnable) throws IOException {
        ioLoop.register(socketChannel, SelectionKey.OP_CONNECT, sKey -> {
                try {
                    boolean b = socketChannel.finishConnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                sKey.interestOps(0);
                sKey.attach(null);
//                sKey.cancel();

                runnable.run();
            });

        socketChannel.connect(address);
    }

    public void write(ByteBuffer wb, Consumer<Integer> writeConsumer) throws IOException {
        ioLoop.register(socketChannel, SelectionKey.OP_WRITE, new SelectionKeyConsumer() {

            @Override
            public void accept(SelectionKey sKey) {
                int writeBytesNum = 0;
                try {
                    writeBytesNum = socketChannel.write(wb);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                writeConsumer.accept(writeBytesNum);
            }
        });
    }

    public void read(ByteBuffer rb, Consumer<Integer> readConsumer) throws IOException {
        ioLoop.register(socketChannel, SelectionKey.OP_READ, new SelectionKeyConsumer() {

            @Override
            public void accept(SelectionKey sKey) {
                int readBytesNum = 0;
                try {
                    readBytesNum = socketChannel.write(rb);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                readConsumer.accept(readBytesNum);
            }
        });
    }

    public void close() throws IOException {
        socketChannel.close();
    }
}
