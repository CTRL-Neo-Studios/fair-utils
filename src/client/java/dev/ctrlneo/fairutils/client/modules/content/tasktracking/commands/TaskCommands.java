package dev.ctrlneo.fairutils.client.modules.content.tasktracking.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.Task;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.TaskObjective;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.objectives.ItemCollectionObjective;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.objectives.LocationObjective;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.utility.TaskStorage;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

/**
 * Command handler for task-related commands
 */
public class TaskCommands {
    private final TaskStorage taskStorage;

    public TaskCommands(TaskStorage taskStorage) {
        this.taskStorage = taskStorage;

        // Register commands
        ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher,
            CommandRegistryAccess registryAccess) {
        // Root command
        dispatcher.register(literal("task")
                // List tasks
                .then(literal("list")
                        .executes(this::listTasks))

                // Create task
                .then(literal("create")
                        .then(argument("title", StringArgumentType.string())
                                .then(argument("description", StringArgumentType.greedyString())
                                        .executes(this::createTask))))

                // Delete task
                .then(literal("delete")
                        .then(argument("index", IntegerArgumentType.integer(1))
                                .executes(this::deleteTask)))

                // Clear all tasks
                .then(literal("clear")
                        .executes(this::clearTasks))

                // Add item collection objective
                .then(literal("add-item-objective")
                        .then(argument("taskIndex", IntegerArgumentType.integer(1))
                                .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                        .then(argument("amount", IntegerArgumentType.integer(1))
                                                .then(argument("description", StringArgumentType.greedyString())
                                                        .executes(this::addItemObjective))))))

                // Add location objective
                .then(literal("add-location-objective")
                        .then(argument("taskIndex", IntegerArgumentType.integer(1))
                                .then(argument("radius", IntegerArgumentType.integer(1))
                                        .then(argument("description", StringArgumentType.greedyString())
                                                .executes(this::addLocationObjective)))))

                // Add area clearing objective
                .then(literal("add-area-objective")
                        .then(argument("taskIndex", IntegerArgumentType.integer(1))
                                .then(argument("description", StringArgumentType.greedyString())
                                        .executes(this::addAreaObjective)))));
    }

    private int listTasks(CommandContext<FabricClientCommandSource> context) {
        List<Task> tasks = taskStorage.getAllTasks();

        if (tasks.isEmpty()) {
            context.getSource().sendFeedback(Text.literal("You have no tasks."));
            return 0;
        }

        context.getSource().sendFeedback(Text.literal("=== Your Tasks ==="));

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String completionStatus = task.isCompleted() ? "[COMPLETED] " : "";
            context.getSource().sendFeedback(Text.literal((i + 1) + ". " + completionStatus + task.getTitle()));

            // Show objectives if there are any
            if (!task.getObjectives().isEmpty()) {
                for (TaskObjective objective : task.getObjectives()) {
                    String status = objective.isCompleted() ? "✓ " : "□ ";
                    context.getSource().sendFeedback(Text.literal("  " + status + objective.getDescription() +
                            " (" + objective.getProgressText().getString() + ")"));
                }
            }
        }

        return 1;
    }

    private int createTask(CommandContext<FabricClientCommandSource> context) {
        String title = StringArgumentType.getString(context, "title");
        String description = StringArgumentType.getString(context, "description");

        Task newTask = new Task(title, description);
        taskStorage.addTask(newTask);

        context.getSource().sendFeedback(Text.literal("Created task: " + title));
        return 1;
    }

    private int deleteTask(CommandContext<FabricClientCommandSource> context) {
        int index = IntegerArgumentType.getInteger(context, "index") - 1; // Convert to 0-based
        List<Task> tasks = taskStorage.getAllTasks();

        if (index < 0 || index >= tasks.size()) {
            context.getSource().sendError(Text.literal("Invalid task index."));
            return 0;
        }

        Task taskToRemove = tasks.get(index);
        taskStorage.removeTask(taskToRemove.getId());

        context.getSource().sendFeedback(Text.literal("Deleted task: " + taskToRemove.getTitle()));
        return 1;
    }

    private int clearTasks(CommandContext<FabricClientCommandSource> context) {
        List<Task> tasks = taskStorage.getAllTasks();
        int count = tasks.size();

        for (Task task : new ArrayList<>(tasks)) {
            taskStorage.removeTask(task.getId());
        }

        context.getSource().sendFeedback(Text.literal("Cleared " + count + " tasks."));
        return 1;
    }

    private int addItemObjective(CommandContext<FabricClientCommandSource> context) {
        int taskIndex = IntegerArgumentType.getInteger(context, "taskIndex") - 1; // Convert to 0-based
        Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        String description = StringArgumentType.getString(context, "description");

        List<Task> tasks = taskStorage.getAllTasks();

        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            context.getSource().sendError(Text.literal("Invalid task index."));
            return 0;
        }

        Task task = tasks.get(taskIndex);
        task.addObjective(new ItemCollectionObjective(task.getId(), description, item, amount));
        taskStorage.saveTasks();

        context.getSource().sendFeedback(Text.literal("Added item collection objective to task: " + task.getTitle()));
        return 1;
    }

    private int addLocationObjective(CommandContext<FabricClientCommandSource> context) {
        int taskIndex = IntegerArgumentType.getInteger(context, "taskIndex") - 1; // Convert to 0-based
        int radius = IntegerArgumentType.getInteger(context, "radius");
        String description = StringArgumentType.getString(context, "description");

        List<Task> tasks = taskStorage.getAllTasks();

        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            context.getSource().sendError(Text.literal("Invalid task index."));
            return 0;
        }

        // Use the player's current position
        Vec3d playerPos = context.getSource().getPlayer().getPos();
        Task task = tasks.get(taskIndex);
        task.addObjective(new LocationObjective(task.getId(), description, playerPos, radius));
        taskStorage.saveTasks();

        context.getSource().sendFeedback(
                Text.literal("Added location objective at your current position to task: " + task.getTitle()));
        return 1;
    }

    private int addAreaObjective(CommandContext<FabricClientCommandSource> context) {
        int taskIndex = IntegerArgumentType.getInteger(context, "taskIndex") - 1; // Convert to 0-based
        String description = StringArgumentType.getString(context, "description");

        List<Task> tasks = taskStorage.getAllTasks();

        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            context.getSource().sendError(Text.literal("Invalid task index."));
            return 0;
        }

        // This will set the first position at the player's feet
        // Players need to use /task set-area-end to set the second position
        BlockPos playerPos = context.getSource().getPlayer().getBlockPos();
        Task task = tasks.get(taskIndex);

        // Store temporary position in a field or map until the second position is set
        AreaSelectionManager.setStartPos(task.getId(), playerPos);

        context.getSource().sendFeedback(
                Text.literal("Set area start position. Use /task set-area-end to complete the area selection."));
        return 1;
    }

    /**
     * Helper class to manage area selection points
     */
    private static class AreaSelectionManager {
        private static final Map<UUID, BlockPos> startPositions = new HashMap<>();

        public static void setStartPos(UUID taskId, BlockPos pos) {
            startPositions.put(taskId, pos);
        }

        public static Optional<BlockPos> getStartPos(UUID taskId) {
            return Optional.ofNullable(startPositions.get(taskId));
        }

        public static void clearStartPos(UUID taskId) {
            startPositions.remove(taskId);
        }
    }
}