package dev.ctrlneo.fairutils.client.modules.content.tasktracking;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

    // Transient fields that shouldn't be serialized
    private transient float cachedProgress = -1;

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
        cachedProgress = -1; // Invalidate cache
    }

    /**
     * Updates the state of this task and its objectives
     */
    public void tick(PlayerEntity player) {
        if (completed)
            return;

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

        // Invalidate progress cache
        cachedProgress = -1;
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
        // Use cached value if available
        if (cachedProgress >= 0)
            return cachedProgress;

        if (objectives.isEmpty())
            return 0.0f;

        float completedCount = 0;
        for (TaskObjective objective : objectives) {
            if (objective.isCompleted()) {
                completedCount += 1f / objectives.stream().count();
            } else if (objective.getProgressPercentage() > 0) {
                completedCount += ((objective.getProgressPercentage() / 100) / objectives.stream().count());
            }
        }

        if (cachedProgress != completedCount) cachedProgress = completedCount;
        return cachedProgress;
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

    /**
     * Convert this task to a JsonObject for safer serialization
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id.toString());
        json.addProperty("title", title);
        json.addProperty("description", description);
        json.addProperty("completed", completed);
        json.addProperty("visible", visible);
        json.addProperty("creationTime", creationTime);

        JsonArray objectivesArray = new JsonArray();
        for (TaskObjective objective : objectives) {
            // Let each objective handle its own serialization
            JsonObject objJson = new JsonObject();
            objJson.addProperty("type", objective.getClass().getName());
            objJson.addProperty("description", objective.getDescription());
            objJson.addProperty("completed", objective.isCompleted());

            // Let the objective add its type-specific properties
            objective.addPropertiesToJson(objJson);

            objectivesArray.add(objJson);
        }
        json.add("objectives", objectivesArray);

        return json;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Task task = (Task) obj;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}