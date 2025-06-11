package dev.ctrlneo.fairutils.client.lib.reactivity.foundation;

// Reactive System Core
public interface ReactiveSystem {
    void link(ReactiveNode dep, ReactiveNode sub);

    Link unlink(Link link, ReactiveNode sub);

    void propagate(Link link);

    boolean checkDirty(Link link, ReactiveNode sub);

    void startTracking(ReactiveNode sub);

    void endTracking(ReactiveNode sub);

    void shallowPropagate(Link link);
}
