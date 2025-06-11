package dev.ctrlneo.fairutils.client.lib.reactivity.foundation;

// System Callbacks
public interface SystemCallbacks {
    boolean update(ReactiveNode sub);

    void notify(ReactiveNode sub);

    void unwatched(ReactiveNode node);
}
