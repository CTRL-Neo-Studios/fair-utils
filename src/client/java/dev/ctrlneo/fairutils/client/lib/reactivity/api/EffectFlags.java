package dev.ctrlneo.fairutils.client.lib.reactivity.api;

// Effect Flags
public enum EffectFlags {
    Queued(1 << 6);
    public final int mask;

    EffectFlags(int mask) {
        this.mask = mask;
    }
}
