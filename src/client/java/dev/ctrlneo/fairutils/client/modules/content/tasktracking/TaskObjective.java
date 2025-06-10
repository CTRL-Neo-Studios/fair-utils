package dev.ctrlneo.fairutils.client.modules.content.tasktracking;

import com.google.gson.JsonObject;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.event.TaskProgressChangedEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

/**
 * Abstract class representing a single objective within a task
 */
public abstract class TaskObjective {
    private final String description;
    private boolean completed;
    private UUID id;
    private UUID parentTask;

    // Transient fields that shouldn't be serialized
    private transient float cachedProgressPercentage = -1;
    private transient float _lastProgressPercentage = -1;

    public TaskObjective(UUID parentTaskId, String description) {
        this.description = description;
        this.completed = false;
        this.parentTask = parentTaskId;
    }

    public TaskObjective(String description) {
        this(null, description);
    }

    /**
     * Check if the objective's condition has been met by the player
     */
    public abstract void checkCondition(PlayerEntity player);

    /**
     * Get the current progress percentage (0-100) of this objective
     */
    public abstract float getProgressPercentage();

    /**
     * Get a Text representation of this objective's progress
     */
    public abstract Text getProgressText();

    /**
     * Add type-specific properties to the JSON object for serialization
     * This should be overridden by implementing classes to add their specific
     * fields
     */
    public void addPropertiesToJson(JsonObject json) {
        if (this.parentTask != null)
            json.addProperty("parentTask", this.parentTask.toString());
        else
            json.addProperty("parentTask", "");
        // Base implementation adds nothing
        // Subclasses should override this to add their specific properties
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed) {
            cachedProgressPercentage = 100f; // When completed, progress is always 100%
        } else {
            cachedProgressPercentage = -1; // Invalidate cache
        }
    }

    /**
     * Reset the cached progress
     */
    protected void invalidateProgressCache() {
        cachedProgressPercentage = -1;
        TaskProgressChangedEvent.EVENT.invoker().onCallback(getParentTask());
    }

    /**
     * Get cached progress or calculate it
     */
    protected float getCachedOrCalculateProgress(float calculatedProgress) {
        if (cachedProgressPercentage >= 0) {
            return cachedProgressPercentage;
        }

        cachedProgressPercentage = calculatedProgress;
        return cachedProgressPercentage;
    }

    protected UUID getParentTask() {
        return parentTask;
    }

    protected boolean checkIfProgressChanged() {
        boolean changed = _lastProgressPercentage != this.getProgressPercentage();
        if (changed)
            TaskProgressChangedEvent.EVENT.invoker().onCallback(getParentTask());
        return changed;
    }
}