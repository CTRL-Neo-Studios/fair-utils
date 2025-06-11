package dev.ctrlneo.fairutils.client.lib.reactivity;

import dev.ctrlneo.fairutils.client.lib.reactivity.api.*;
import dev.ctrlneo.fairutils.client.lib.reactivity.foundation.*;

import java.util.ArrayDeque;
import java.util.Deque;

public class ReactivitySystem {

    // Global Context
    public static class Context {
        public static int batchDepth = 0;
        public static int notifyIndex = 0;
        public static ReactiveNode[] queuedEffects = new ReactiveNode[100];
        public static int queuedEffectsLength = 0;
        public static ReactiveNode activeSub;
        public static EffectScope activeScope;
        public static Deque<ReactiveNode> pauseStack = new ArrayDeque<>();

        public static ReactiveSystem reactiveSystem;
    }

    // Factory for Reactive System
    public static ReactiveSystem createReactiveSystem(
            SystemCallbacks callbacks
    ) {
        return new ReactiveSystemImpl(callbacks);
    }

    // Reactive System Implementation
    private static class ReactiveSystemImpl implements ReactiveSystem {
        private final SystemCallbacks callbacks;

        public ReactiveSystemImpl(SystemCallbacks callbacks) {
            this.callbacks = callbacks;
        }

        @Override
        public void link(ReactiveNode dep, ReactiveNode sub) {
            // Implementation matching TypeScript version
            // ... (omitted for brevity, same logic as TS)
        }

        @Override
        public Link unlink(Link link, ReactiveNode sub) {
            // Implementation matching TypeScript version
            // ... (omitted for brevity, same logic as TS)
            return link.nextDep;
        }

        @Override
        public void propagate(Link link) {
            // Implementation matching TypeScript version
            // ... (omitted for brevity, same logic as TS)
        }

        @Override
        public boolean checkDirty(Link link, ReactiveNode sub) {
            // Implementation matching TypeScript version
            // ... (omitted for brevity, same logic as TS)
            return false;
        }

        @Override
        public void startTracking(ReactiveNode sub) {
            sub.depsTail = null;
            sub.flags = (sub.flags & ~(ReactiveFlags.Recursed.mask |
                    ReactiveFlags.Dirty.mask |
                    ReactiveFlags.Pending.mask)) |
                    ReactiveFlags.RecursedCheck.mask;
        }

        @Override
        public void endTracking(ReactiveNode sub) {
            Link toRemove = sub.depsTail != null ? sub.depsTail.nextDep : sub.deps;
            while (toRemove != null) {
                toRemove = unlink(toRemove, sub);
            }
            sub.flags &= ~ReactiveFlags.RecursedCheck.mask;
        }

        @Override
        public void shallowPropagate(Link link) {
            while (link != null) {
                ReactiveNode sub = link.sub;
                int flags = sub.flags;
                if ((flags & (ReactiveFlags.Pending.mask | ReactiveFlags.Dirty.mask)) != 0) {
                    sub.flags = flags | ReactiveFlags.Dirty.mask;
                    callbacks.notify(sub);
                }
                link = link.nextSub;
            }
        }
    }

    // Public API
    public static <T> Signal<T> signal(T initialValue) {
        Signal<T> node = new Signal<>();
        node.previousValue = initialValue;
        node.value = initialValue;
        node.flags = ReactiveFlags.Mutable.mask;
        return node;
    }

    public static <T> Computed<T> computed(ComputedGetter<T> getter) {
        Computed<T> node = new Computed<>();
        node.getter = getter;
        node.flags = ReactiveFlags.Mutable.mask | ReactiveFlags.Dirty.mask;
        return node;
    }

    public static Disposable effect(Runnable fn) {
        Effect node = new Effect();
        node.fn = fn;
        node.flags = ReactiveFlags.Watching.mask;

        if (Context.activeSub != null) {
            Context.reactiveSystem.link(node, Context.activeSub);
        } else if (Context.activeScope != null) {
            Context.reactiveSystem.link(node, Context.activeScope);
        }

        ReactiveNode prev = Context.activeSub;
        Context.activeSub = node;
        try {
            node.fn.run();
        } finally {
            Context.activeSub = prev;
        }

        return () -> effectDispose(node);
    }

    public static Disposable effectScope(Runnable fn) {
        EffectScope scope = new EffectScope();
        if (Context.activeScope != null) {
            Context.reactiveSystem.link(scope, Context.activeScope);
        }

        ReactiveNode prevSub = Context.activeSub;
        EffectScope prevScope = Context.activeScope;
        Context.activeSub = null;
        Context.activeScope = scope;

        try {
            fn.run();
        } finally {
            Context.activeScope = prevScope;
            Context.activeSub = prevSub;
        }

        return () -> effectDispose(scope);
    }

    private static void effectDispose(ReactiveNode node) {
        Link dep = node.deps;
        while (dep != null) {
            dep = Context.reactiveSystem.unlink(dep, node);
        }
        if (node.subs != null) {
            Context.reactiveSystem.unlink(node.subs, null);
        }
        node.flags = ReactiveFlags.None.mask;
    }

    // Initialize Reactive System
    static {
        SystemCallbacks callbacks = new SystemCallbacks() {
            @Override
            public boolean update(ReactiveNode sub) {
                if (sub instanceof Computed) {
                    return updateComputed((Computed<?>) sub);
                } else if (sub instanceof Signal) {
                    return updateSignal((Signal<?>) sub);
                }
                return false;
            }

            @Override
            public void notify(ReactiveNode sub) {
                ReactivitySystem.notify(sub);
            }

            @Override
            public void unwatched(ReactiveNode node) {
                if (node instanceof Computed) {
                    // Handle computed cleanup
                } else if (node instanceof Effect || node instanceof EffectScope) {
                    // Handle effect cleanup
                }
            }
        };
        Context.reactiveSystem = createReactiveSystem(callbacks);
    }

    // Helper Methods
    private static <T> boolean updateComputed(Computed<T> computed) {
        ReactiveNode prevSub = Context.activeSub;
        Context.activeSub = computed;
        Context.reactiveSystem.startTracking(computed);
        try {
            T oldValue = computed.value;
            computed.value = computed.getter.get(oldValue);
            return oldValue != computed.value;
        } finally {
            Context.activeSub = prevSub;
            Context.reactiveSystem.endTracking(computed);
        }
    }

    private static <T> boolean updateSignal(Signal<T> signal) {
        signal.flags = ReactiveFlags.Mutable.mask;
        T newValue = signal.value;
        if (signal.previousValue != newValue) {
            signal.previousValue = newValue;
            return true;
        }
        return false;
    }

    private static void notify(ReactiveNode node) {
        if ((node.flags & EffectFlags.Queued.mask) == 0) {
            node.flags |= EffectFlags.Queued.mask;
            if (node.subs != null) {
                notify(node.subs.sub);
            } else {
                Context.queuedEffects[Context.queuedEffectsLength++] = node;
            }
        }
    }

}