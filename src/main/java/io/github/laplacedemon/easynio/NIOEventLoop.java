package io.github.laplacedemon.easynio;

import io.github.laplacedemon.easynio.queue.MPSCQueue;
import io.github.laplacedemon.easynio.queue.MPSCQueueFactory;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

public class NIOEventLoop implements Runnable {
    private Selector selector;
    private MPSCQueue<RegisterEvent> userEventQueue = MPSCQueueFactory.createMPSCConcurrentQueue();

    public NIOEventLoop() throws IOException {
        selector = Selector.open();
    }

    public void register(SelectableChannel sch, int ops, SelectionKeyConsumer selectionKeyConsumer)  {
        userEventQueue.offer(new RegisterEvent(sch, ops, selectionKeyConsumer));
        selector.wakeup();
    }

    @Override
    public void run() {
        while (true) {
            try {
                int select = selector.select();

                while (true) {
                    RegisterEvent registerEvent = userEventQueue.poll();
                    if (registerEvent == null) {
                        break;
                    }
                    try {
                        registerEvent.getSelectableChannel().register(selector, registerEvent.getOps(), registerEvent.getSelectionKeyConsumer());
                    } catch (ClosedChannelException e) {
                        e.printStackTrace();
                    }
                }

                if (select == 0) {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()) {
                try {
                    SelectionKey sKey = it.next();
                    if (sKey.isConnectable()) {
                        Object callback = sKey.attachment();
                        ((SelectionKeyConsumer) callback).accept(sKey);
                    } else if (sKey.isAcceptable()) {
                        Object callback = sKey.attachment();
                        ((SelectionKeyConsumer) callback).accept(sKey);
                    } else if (sKey.isReadable()) {
                        Object callback = sKey.attachment();
                        ((SelectionKeyConsumer) callback).accept(sKey);
                    } else if (sKey.isWritable()) {
                        Object callback = sKey.attachment();
                        ((SelectionKeyConsumer) callback).accept(sKey);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                } finally {
                    it.remove();
                }
            }
        }
    }

}
