package io.github.laplacedemon.easynio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Channel {
    private SocketChannel socketChannel;
    private NIOEventLoop ioLoop;
    private AtomicBoolean complete = new AtomicBoolean();

    public SocketChannel javaChannel() {
        return socketChannel;
    }

    public Channel(NIOEventLoop ioLoop) {
        this.ioLoop = ioLoop;
    }

    public void open(SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        socketChannel.configureBlocking(false);
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

                runnable.run();
            });

        socketChannel.connect(address);
    }

    public void write(ByteBuffer wb, Consumer<Integer> writeConsumer) throws IOException {
        ioLoop.register(socketChannel, SelectionKey.OP_WRITE, new SelectionKeyConsumer() {

            int hasWriteBytesNum;

            @Override
            public void accept(SelectionKey sKey) {
                int writeBytesNum = 0;
                try {
                    writeBytesNum = socketChannel.write(wb);
                    hasWriteBytesNum += writeBytesNum;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (wb.remaining() == 0) {
                    writeConsumer.accept(writeBytesNum);
                    sKey.interestOps(0);
                    sKey.attach(null);
                }
            }
        });
    }

    public void read(ByteBuffer rb, Consumer<Integer> readConsumer) throws IOException {
        complete.set(false);
        ioLoop.register(socketChannel, SelectionKey.OP_READ, new SelectionKeyConsumer() {

            @Override
            public void accept(SelectionKey sKey) {
                int readBytesNum = 0;
                try {
                    readBytesNum = socketChannel.read(rb);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                readConsumer.accept(readBytesNum);

                if (complete.get()) {
                    sKey.interestOps(0);
                    sKey.attach(null);
                }
            }
        });
    }

    public void readComplete() {
        complete.set(true);
    }

    public void close() throws IOException {
        complete.set(false);
        socketChannel.close();
    }
}
