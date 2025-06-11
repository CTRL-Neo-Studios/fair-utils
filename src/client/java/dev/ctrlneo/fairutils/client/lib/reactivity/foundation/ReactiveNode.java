package dev.ctrlneo.fairutils.client.lib.reactivity.foundation;

// Reactive Node Base Class
public abstract class ReactiveNode {
    public Link deps;
    public Link depsTail;
    public Link subs;
    public Link subsTail;
    public int flags;
}
