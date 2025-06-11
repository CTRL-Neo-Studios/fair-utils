package dev.ctrlneo.fairutils.client.lib.reactivity.api;

import dev.ctrlneo.fairutils.client.lib.reactivity.foundation.ReactiveNode;

// Signal
public class Signal<T> extends ReactiveNode {
    public T previousValue;
    public T value;
}
