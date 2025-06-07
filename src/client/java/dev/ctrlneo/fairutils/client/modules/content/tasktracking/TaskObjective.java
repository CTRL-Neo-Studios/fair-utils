package dev.ctrlneo.fairutils.client.modules.content.tasktracking;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

/**
 * Abstract class representing a single objective within a task
 */
public abstract class TaskObjective {
    private final String description;
    private boolean completed;
    
    public TaskObjective(String description) {
        this.description = description;
        this.completed = false;
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

    public String getDescription() {
        return description;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}