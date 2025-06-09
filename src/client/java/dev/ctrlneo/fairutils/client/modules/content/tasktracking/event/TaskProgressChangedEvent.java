package dev.ctrlneo.fairutils.client.modules.content.tasktracking.event;

import dev.ctrlneo.fairutils.client.event.Event;
import dev.ctrlneo.fairutils.client.event.EventFactory;

import java.util.UUID;

public interface TaskProgressChangedEvent {
    void onCallback(UUID taskId);

    Event<TaskProgressChangedEvent> EVENT = EventFactory.createArrayBacked(TaskProgressChangedEvent.class, (listeners) -> (taskId) -> {
        for (TaskProgressChangedEvent event : listeners) {
            event.onCallback(taskId);
        }
    });
}
