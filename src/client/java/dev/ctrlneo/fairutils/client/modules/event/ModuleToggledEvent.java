package dev.ctrlneo.fairutils.client.modules.event;

import dev.ctrlneo.fairutils.client.event.Event;
import dev.ctrlneo.fairutils.client.event.EventFactory;

public interface ModuleToggledEvent {
    void onCallback(boolean toggled, String moduleName, String moduleId);

    Event<ModuleToggledEvent> EVENT = EventFactory.createArrayBacked(ModuleToggledEvent.class, (listeners) -> (toggled, moduleName, moduleId) -> {
        for (ModuleToggledEvent event : listeners) {
            event.onCallback(toggled, moduleName, moduleId);
        }
    });
}
