package dev.ctrlneo.fairutils.client.modules.content.tasktracking.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.ctrlneo.fairutils.client.FairUtilsClient;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.Task;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.objectives.ItemCollectionObjective;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.objectives.LocationObjective;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.objectives.AreaClearingObjective;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages all player tasks, including saving and loading
 */
public class TaskManager {
    private final List<Task> tasks = new ArrayList<>();
    private final File tasksFile;
    private final Gson gson;

    public TaskManager() {
        tasksFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "fairutils_tasks.json");

        // Create a simple Gson instance for pretty printing
        gson = new GsonBuilder().setPrettyPrinting().create();

        loadTasks();
    }

    /**
     * Add a new task
     */
    public void addTask(Task task) {
        tasks.add(task);
        saveTasks();
    }

    /**
     * Remove a task by its ID
     */
    public boolean removeTask(UUID taskId) {
        boolean removed = tasks.removeIf(task -> task.getId().equals(taskId));
        if (removed) {
            saveTasks();
        }
        return removed;
    }

    /**
     * Get a task by its ID
     */
    public Optional<Task> getTask(UUID taskId) {
        return tasks.stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst();
    }

    /**
     * Get all tasks
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Get visible tasks (not completed or hidden)
     */
    public List<Task> getVisibleTasks() {
        List<Task> visibleTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isVisible()) {
                visibleTasks.add(task);
            }
        }
        return visibleTasks;
    }

    /**
     * Get completed tasks
     */
    public List<Task> getCompletedTasks() {
        List<Task> completedTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isCompleted()) {
                completedTasks.add(task);
            }
        }
        return completedTasks;
    }

    /**
     * Update all tasks
     */
    public void tick() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null)
            return;

        for (Task task : tasks) {
            task.tick(player);
        }
    }

    /**
     * Load tasks from file
     */
    public void loadTasks() {
        if (!tasksFile.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(tasksFile)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (root.isJsonArray()) {
                JsonArray tasksArray = root.getAsJsonArray();

                tasks.clear();

                for (JsonElement taskElement : tasksArray) {
                    if (taskElement.isJsonObject()) {
                        JsonObject taskJson = taskElement.getAsJsonObject();
                        Task task = deserializeTask(taskJson);
                        if (task != null) {
                            tasks.add(task);
                        }
                    }
                }

                FairUtilsClient.LOGGER.info("Loaded " + tasks.size() + " tasks");
            }
        } catch (IOException e) {
            FairUtilsClient.LOGGER.error("Failed to load tasks", e);
        }
    }

    /**
     * Save tasks to file
     */
    public void saveTasks() {
        try (FileWriter writer = new FileWriter(tasksFile)) {
            JsonArray tasksArray = new JsonArray();

            for (Task task : tasks) {
                tasksArray.add(task.toJson());
            }

            gson.toJson(tasksArray, writer);
            FairUtilsClient.LOGGER.info("Saved " + tasks.size() + " tasks");
        } catch (IOException e) {
            FairUtilsClient.LOGGER.error("Failed to save tasks", e);
        }
    }

    /**
     * Deserialize a task from JSON
     */
    private Task deserializeTask(JsonObject json) {
        try {
            String title = json.get("title").getAsString();
            String description = json.get("description").getAsString();

            Task task = new Task(title, description);

            // Set properties
            if (json.has("id")) {
                UUID id = UUID.fromString(json.get("id").getAsString());
                // No setter for ID, but it's already set in constructor
            }

            if (json.has("completed")) {
                task.setCompleted(json.get("completed").getAsBoolean());
            }

            if (json.has("visible")) {
                task.setVisible(json.get("visible").getAsBoolean());
            }

            if (json.has("creationTime")) {
                // Creation time is already set in constructor
            }

            // Load objectives
            if (json.has("objectives") && json.get("objectives").isJsonArray()) {
                JsonArray objectivesArray = json.getAsJsonArray("objectives");

                for (JsonElement objectiveElement : objectivesArray) {
                    if (objectiveElement.isJsonObject()) {
                        JsonObject objectiveJson = objectiveElement.getAsJsonObject();
                        deserializeObjective(objectiveJson, task);
                    }
                }
            }

            return task;
        } catch (Exception e) {
            FairUtilsClient.LOGGER.error("Error deserializing task", e);
            return null;
        }
    }

    /**
     * Deserialize an objective from JSON and add it to a task
     */
    private void deserializeObjective(JsonObject json, Task task) {
        try {
            if (!json.has("type")) {
                return;
            }

            String type = json.get("type").getAsString();
            String description = json.get("description").getAsString();
            boolean completed = json.get("completed").getAsBoolean();

            // Create the appropriate objective type
            if (type.contains("ItemCollectionObjective")) {
                // Deserialize item collection objective
                String itemId = json.get("itemId").getAsString();
                Item item = Registries.ITEM.get(Identifier.tryParse(itemId));
                int targetAmount = json.get("targetAmount").getAsInt();
                int currentAmount = json.get("currentAmount").getAsInt();

                ItemCollectionObjective objective = new ItemCollectionObjective(task.getId(), description, item, targetAmount);
                objective.setCompleted(completed);
                objective.setCurrentAmount(currentAmount);

                task.addObjective(objective);
            } else if (type.contains("LocationObjective")) {
                // Deserialize location objective
                double x = json.get("x").getAsDouble();
                double y = json.get("y").getAsDouble();
                double z = json.get("z").getAsDouble();
                double radius = json.get("radius").getAsDouble();

                LocationObjective objective = new LocationObjective(task.getId(), description, new Vec3d(x, y, z), radius);
                objective.setCompleted(completed);

                if (json.has("visited")) {
                    objective.setVisited(json.get("visited").getAsBoolean());
                }

                if (json.has("closestDistance")) {
                    objective.setClosestDistance(json.get("closestDistance").getAsDouble());
                }

                task.addObjective(objective);
            } else if (type.contains("AreaClearingObjective")) {
                // Deserialize area clearing objective
                int x1 = json.get("x1").getAsInt();
                int y1 = json.get("y1").getAsInt();
                int z1 = json.get("z1").getAsInt();
                int x2 = json.get("x2").getAsInt();
                int y2 = json.get("y2").getAsInt();
                int z2 = json.get("z2").getAsInt();

                AreaClearingObjective objective = new AreaClearingObjective(
                        task.getId(),
                        description,
                        new BlockPos(x1, y1, z1),
                        new BlockPos(x2, y2, z2));
                objective.setCompleted(completed);

                if (json.has("totalBlocksToMine")) {
                    objective.setTotalBlocksToMine(json.get("totalBlocksToMine").getAsInt());
                }

                if (json.has("blocksRemaining")) {
                    objective.setBlocksRemaining(json.get("blocksRemaining").getAsInt());
                }

                if (json.has("initialized")) {
                    objective.setInitialized(json.get("initialized").getAsBoolean());
                }

                task.addObjective(objective);
            }
            // Add more objective types as needed

        } catch (Exception e) {
            FairUtilsClient.LOGGER.error("Error deserializing objective", e);
        }
    }
}