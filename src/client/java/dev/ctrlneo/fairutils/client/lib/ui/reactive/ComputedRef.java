package dev.ctrlneo.fairutils.client.lib.ui.reactive;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A computed reactive value that depends on other reactive values
 * 
 * @param <T> The type of the value
 */
public class ComputedRef<T> {
    private final Supplier<T> computeFn;
    private T cachedValue;
    private final List<Consumer<T>> listeners = new ArrayList<>();
    private boolean dirty = true;

    /**
     * Create a new computed ref
     * 
     * @param computeFn A function that computes the value
     */
    public ComputedRef(Supplier<T> computeFn) {
        this.computeFn = computeFn;
    }

    /**
     * Get the current value, computing it if necessary
     * 
     * @return The current value
     */
    public T value() {
        if (dirty) {
            cachedValue = computeFn.get();
            dirty = false;
        }
        return cachedValue;
    }

    /**
     * Mark this computed ref as dirty
     */
    public void invalidate() {
        if (!dirty) {
            dirty = true;
            notifyListeners();
        }
    }

    /**
     * Add a listener that will be called when the value changes
     * 
     * @param listener The listener
     * @return This ComputedRef for chaining
     */
    public ComputedRef<T> watch(Consumer<T> listener) {
        listeners.add(listener);
        // Immediately call the listener with the current value
        listener.accept(value());
        return this;
    }

    /**
     * Remove a listener
     * 
     * @param listener The listener to remove
     * @return This ComputedRef for chaining
     */
    public ComputedRef<T> unwatch(Consumer<T> listener) {
        listeners.remove(listener);
        return this;
    }

    /**
     * Notify all listeners of the current value
     */
    private void notifyListeners() {
        T value = value();
        for (Consumer<T> listener : listeners) {
            listener.accept(value);
        }
    }

    /**
     * Create a new ComputedRef
     * 
     * @param computeFn A function that computes the value
     * @param <T>       The type of the value
     * @return A new ComputedRef
     */
    public static <T> ComputedRef<T> of(Supplier<T> computeFn) {
        return new ComputedRef<>(computeFn);
    }

    /**
     * Create a new ComputedRef that depends on multiple refs
     * 
     * @param refs      The refs to depend on
     * @param computeFn A function that computes the value from the refs
     * @param <T>       The type of the value
     * @return A new ComputedRef
     */
    public static <T> ComputedRef<T> from(List<Ref<?>> refs, Supplier<T> computeFn) {
        ComputedRef<T> computedRef = new ComputedRef<>(computeFn);

        for (Ref<?> ref : refs) {
            ref.watch(value -> computedRef.invalidate());
        }

        return computedRef;
    }
}