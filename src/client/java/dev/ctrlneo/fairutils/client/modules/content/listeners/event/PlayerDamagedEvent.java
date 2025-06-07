package dev.ctrlneo.fairutils.client.modules.content.listeners.event;

import dev.ctrlneo.fairutils.client.event.Event;
import dev.ctrlneo.fairutils.client.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;

public interface PlayerDamagedEvent {
    void onCallback(double newHealth, double oldHealth, DamageSource damageSource);

    Event<PlayerDamagedEvent> EVENT = EventFactory.createArrayBacked(PlayerDamagedEvent.class, (listeners) -> (newHealth, oldHealth, damageSource) -> {
        for (PlayerDamagedEvent event : listeners) {
            event.onCallback(newHealth, oldHealth, damageSource);
        }
    });
}
