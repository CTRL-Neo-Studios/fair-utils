package dev.ctrlneo.fairutils.client.lib.ui.reactive;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A reactive variable container inspired by Vue's ref system
 * 
 * @param <T> The type of the value
 */
public class Ref<T> {
    private T value;
    private final List<Consumer<T>> listeners = new ArrayList<>();

    /**
     * Create a new Ref with the given value
     * 
     * @param initialValue The initial value
     */
    public Ref(T initialValue) {
        this.value = initialValue;
    }

    /**
     * Get the current value
     * 
     * @return The current value
     */
    public T value() {
        return value;
    }

    /**
     * Set the value and notify listeners
     * 
     * @param newValue The new value
     */
    public void setValue(T newValue) {
        if ((this.value == null && newValue != null) ||
                (this.value != null && !this.value.equals(newValue))) {
            this.value = newValue;
            notifyListeners();
        }
    }

    /**
     * Add a listener that will be called when the value changes
     * 
     * @param listener The listener
     * @return This Ref for chaining
     */
    public Ref<T> watch(Consumer<T> listener) {
        listeners.add(listener);
        // Immediately call the listener with the current value
        listener.accept(value);
        return this;
    }

    /**
     * Remove a listener
     * 
     * @param listener The listener to remove
     * @return This Ref for chaining
     */
    public Ref<T> unwatch(Consumer<T> listener) {
        listeners.remove(listener);
        return this;
    }

    /**
     * Notify all listeners of the current value
     */
    private void notifyListeners() {
        for (Consumer<T> listener : listeners) {
            listener.accept(value);
        }
    }

    /**
     * Create a new Ref derived from this one
     * 
     * @param mapper A function that maps this Ref's value to a new value
     * @param <R>    The type of the new value
     * @return A new Ref that updates when this one does
     */
    public <R> Ref<R> map(java.util.function.Function<T, R> mapper) {
        Ref<R> derivedRef = new Ref<>(mapper.apply(value));
        this.watch(value -> derivedRef.setValue(mapper.apply(value)));
        return derivedRef;
    }

    /**
     * Create a new Ref with the given value
     * 
     * @param initialValue The initial value
     * @param <T>          The type of the value
     * @return A new Ref
     */
    public static <T> Ref<T> of(T initialValue) {
        return new Ref<>(initialValue);
    }
}