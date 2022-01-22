package io.github.laplacedemon.easynio;

import io.github.laplacedemon.easynio.mock.MockTCPClient;
import io.github.laplacedemon.easynio.mock.MockTCPServer;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ClientChannelWriteTest {

    @Test
    public void write() throws IOException, InterruptedException {
        Bootstrap bootstrap = new Bootstrap().workGroup();
        Channel channel = bootstrap.openChannel();
        channel.connect(new InetSocketAddress("127.0.0.1", 19000), () -> {
            ByteBuffer bb = ByteBuffer.wrap("request:hello".getBytes(StandardCharsets.UTF_8));
            try {
                channel.write(bb, wi -> {
                    System.out.println("has write bytes num: " + wi);

                    ByteBuffer rb = ByteBuffer.allocate(32);
                    try {
                        channel.read(rb, ri -> {
                            try {
                                System.out.println("has read :" + ri + ", content: " + new String(rb.array()));
                                channel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread.sleep(1000 * 10);
    }

}
