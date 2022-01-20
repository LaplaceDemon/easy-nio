package io.github.laplacedemon.easynio.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentQueue<E> implements MPSCQueue<E> {
    private ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<>();

    public boolean offer(E e) {
        return queue.offer(e);
    }

    public E poll() {
        return queue.poll();
    }
}
