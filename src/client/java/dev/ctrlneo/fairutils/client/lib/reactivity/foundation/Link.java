package dev.ctrlneo.fairutils.client.lib.reactivity.foundation;

// Link between nodes
public class Link {
    public ReactiveNode dep;
    public ReactiveNode sub;
    public Link prevSub;
    public Link nextSub;
    public Link prevDep;
    public Link nextDep;
}
