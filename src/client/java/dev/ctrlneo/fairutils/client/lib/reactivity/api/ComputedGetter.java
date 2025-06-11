package dev.ctrlneo.fairutils.client.lib.reactivity.api;

// Computed Getter Functional Interface
@FunctionalInterface
public interface ComputedGetter<T> {
    T get(T previousValue);
}
