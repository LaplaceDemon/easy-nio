package io.github.laplacedemon.easynio.queue;

public interface MPSCQueue<E> {
    boolean offer(E e);
    E poll();
}
