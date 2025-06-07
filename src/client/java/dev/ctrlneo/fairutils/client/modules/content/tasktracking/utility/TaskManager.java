package dev.ctrlneo.fairutils.client.modules.content.tasktracking.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.ctrlneo.fairutils.client.FairUtilsClient;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.Task;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages all player tasks, including saving and loading
 */
public class TaskManager {
    private final List<Task> tasks = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File tasksFile;

    public TaskManager() {
        tasksFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "fairutils_tasks.json");
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
            Type taskListType = new TypeToken<ArrayList<Task>>() {
            }.getType();
            List<Task> loadedTasks = gson.fromJson(reader, taskListType);

            if (loadedTasks != null) {
                tasks.clear();
                tasks.addAll(loadedTasks);
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
            gson.toJson(tasks, writer);
            FairUtilsClient.LOGGER.info("Saved " + tasks.size() + " tasks");
        } catch (IOException e) {
            FairUtilsClient.LOGGER.error("Failed to save tasks", e);
        }
    }
}