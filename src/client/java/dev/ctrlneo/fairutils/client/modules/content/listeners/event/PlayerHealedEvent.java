package dev.ctrlneo.fairutils.client.modules.content.listeners.event;

import dev.ctrlneo.fairutils.client.event.Event;
import dev.ctrlneo.fairutils.client.event.EventFactory;

public interface PlayerHealedEvent {
    void onCallback(double newHealth, double oldHealth);

    Event<PlayerHealedEvent> EVENT = EventFactory.createArrayBacked(PlayerHealedEvent.class, (listeners) -> (newHealth, oldHealth) -> {
        for (PlayerHealedEvent event : listeners) {
            event.onCallback(newHealth, oldHealth);
        }
    });
}
