package io.github.laplacedemon.easynio.queue;

public class MPSCQueueFactory {
    private MPSCQueueFactory() {}

    public static <E> MPSCQueue<E> createMPSCConcurrentQueue() {
        return new ConcurrentQueue<>();
    }

}
