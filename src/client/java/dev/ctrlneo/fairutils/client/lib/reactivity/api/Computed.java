package dev.ctrlneo.fairutils.client.lib.reactivity.api;

import dev.ctrlneo.fairutils.client.lib.reactivity.foundation.ReactiveNode;

// Computed
public class Computed<T> extends ReactiveNode {
    public T value;
    public ComputedGetter<T> getter;
}
