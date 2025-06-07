package dev.ctrlneo.fairutils.client.event;

public interface Event<T> {
    T invoker();
    void register(T listener);
}
