package io.github.laplacedemon.easynio;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Bootstrap {
    private NIOEventLoop acceptEventLoop;
    private NIOEventLoop[] workEventLoops;
    private int nextWorkerIndex;
    private ScheduledExecutorService workExecutorService;
    private ScheduledExecutorService acceptExecutorService;

    public void workGroup(int workThreadNum) throws IOException {
        workEventLoops = new NIOEventLoop[workThreadNum];
        for (int i = 0; i< workThreadNum; i++) {
            workEventLoops[i] = new NIOEventLoop();
        }

        workExecutorService = Executors.newScheduledThreadPool(workThreadNum);

        for (int i = 0; i< workThreadNum; i++) {
            workExecutorService.submit(workEventLoops[i]);
        }
    }

    public Bootstrap workGroup() throws IOException {
        workGroup(Runtime.getRuntime().availableProcessors());
        return this;
    }

    public Bootstrap serverWorkGroup() throws IOException {
        serverWorkGroup(Runtime.getRuntime().availableProcessors());
        return this;
    }

    public Bootstrap serverWorkGroup(int workThreadNum) throws IOException {
        workGroup(workThreadNum);
        acceptEventLoop = new NIOEventLoop();

        acceptExecutorService = Executors.newScheduledThreadPool(1);
        acceptExecutorService.submit(acceptEventLoop);
        return this;
    }

    public Channel openChannel() throws IOException {
        NIOEventLoop nioEventLoop = pickUpWorker();
        Channel channel = new Channel(nioEventLoop);
        channel.open();
        return channel;
    }

    NIOEventLoop pickUpWorker() {
        NIOEventLoop worker = workEventLoops[nextWorkerIndex];
        nextWorkerIndex++;
        if (nextWorkerIndex >= workEventLoops.length) {
            nextWorkerIndex = 0;
        }

        return worker;
    }

    public ChannelAcceptor openChannelAcceptor() throws IOException {
        ChannelAcceptor channelAcceptor = new ChannelAcceptor(acceptEventLoop);
        channelAcceptor.open();
        return channelAcceptor;
    }

    public void shutdown() {
    }
}
