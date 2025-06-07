package dev.ctrlneo.fairutils.client.modules.content.tasktracking;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Represents a trackable task that can be created by players
 */
public class Task {
    private final UUID id;
    private String title;
    private String description;
    private boolean completed;
    private boolean visible = true;
    private final List<TaskObjective> objectives = new ArrayList<>();
    private long creationTime;
    
    public Task(String title, String description) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.completed = false;
        this.creationTime = System.currentTimeMillis();
    }
    
    /**
     * Adds an objective to this task
     */
    public void addObjective(TaskObjective objective) {
        objectives.add(objective);
    }
    
    /**
     * Updates the state of this task and its objectives
     */
    public void tick(PlayerEntity player) {
        if (completed) return;
        
        boolean allCompleted = true;
        for (TaskObjective objective : objectives) {
            objective.checkCondition(player);
            if (!objective.isCompleted()) {
                allCompleted = false;
            }
        }
        
        if (allCompleted && !objectives.isEmpty()) {
            completed = true;
            // Play completion sound or display message
        }
    }
    
    public UUID getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public List<TaskObjective> getObjectives() {
        return objectives;
    }
    
    public long getCreationTime() {
        return creationTime;
    }
    
    /**
     * Calculate the overall progress percentage of this task
     */
    public float getProgress() {
        if (objectives.isEmpty()) return 0.0f;
        
        int completedCount = 0;
        for (TaskObjective objective : objectives) {
            if (objective.isCompleted()) {
                completedCount++;
            } else if (objective.getProgressPercentage() > 0) {
                completedCount += objective.getProgressPercentage() / 100.0f;
            }
        }
        
        return (float) completedCount / objectives.size();
    }
    
    /**
     * Get formatted text representation of the task's current progress
     */
    public Text getProgressText() {
        if (objectives.isEmpty()) {
            return Text.literal("No objectives");
        }
        
        return Text.literal(String.format("%.0f%%", getProgress() * 100));
    }
}