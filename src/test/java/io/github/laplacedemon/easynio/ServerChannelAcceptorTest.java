package io.github.laplacedemon.easynio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class ServerChannelAcceptorTest {

    @Test
    public void accept() throws IOException, InterruptedException {
        int COUNT = 6;
        int PORT = 19010;
        Bootstrap bootstrap = new Bootstrap().serverWorkGroup();
        ChannelAcceptor channelAcceptor = bootstrap.openChannelAcceptor();

        channelAcceptor.bind(new InetSocketAddress(PORT));

        CountDownLatch cdl = new CountDownLatch(COUNT);
        channelAcceptor.loopAccept(ch-> {
            System.out.println("accpet: " + ch);
            cdl.countDown();
        });

        for (int i = 0; i < COUNT; i++) {
            new Socket().connect(new InetSocketAddress("127.0.0.1", PORT));
            Thread.sleep(1000 * 1);
        }

        cdl.await();
    }
}
