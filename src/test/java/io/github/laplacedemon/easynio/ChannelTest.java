package io.github.laplacedemon.easynio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class ChannelTest {

    @Test
    public void connect() throws IOException, InterruptedException {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 19000);
        NIOEventLoop ioEventLoop = new NIOEventLoop();
        ioEventLoop.open();
        new Thread(ioEventLoop).start();

        Channel channel = new Channel(ioEventLoop);
        channel.open();

        CountDownLatch cdl = new CountDownLatch(1);

        channel.connect(address, () -> {
            System.out.println("Connected！！！");
            try {
                channel.close();
                cdl.countDown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        cdl.await();
    }

    @Test
    public void write() throws IOException, InterruptedException {
        CountDownLatch cdl = new CountDownLatch(1);

        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 19000);
        NIOEventLoop ioEventLoop = new NIOEventLoop();
        ioEventLoop.open();
        new Thread(ioEventLoop).start();

        Channel channel = new Channel(ioEventLoop);
        channel.open();

        ByteBuffer bb = ByteBuffer.wrap("helloworld001".getBytes(StandardCharsets.UTF_8));

        channel.connect(address, () -> {
            System.out.println("Connected");

            try {
                channel.write(bb, n -> {
                    System.out.println("write: " + n);

                    try {
                        channel.close();

                        cdl.countDown();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        cdl.await();
    }

    @Test
    public void accept() {
        ChannelAcceptor channelAcceptor = new ChannelAcceptor();
        channelAcceptor.bind(19000);

        channelAcceptor.loopAccept(ch-> {
            System.out.println("client has been accepted. " + ch);
        });
    }
}
