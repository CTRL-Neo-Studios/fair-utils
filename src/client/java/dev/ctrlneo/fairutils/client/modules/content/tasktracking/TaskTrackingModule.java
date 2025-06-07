package dev.ctrlneo.fairutils.client.modules.content.tasktracking;

import dev.ctrlneo.fairutils.client.config.FairUtilsConfig;
import dev.ctrlneo.fairutils.client.modules.UtilityModule;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.gui.TaskOverlayRenderLayer;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.objectives.ItemCollectionObjective;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.utility.TaskManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class TaskTrackingModule extends UtilityModule {
    public static final String MODULE_CATEGORY = "Task Tracking";
    private final TaskManager taskManager = new TaskManager();
    private final TaskOverlayRenderLayer renderer = new TaskOverlayRenderLayer(taskManager);
    private MinecraftClient mc;

    @Override
    public void initialize() {
        super.initialize();
        mc = MinecraftClient.getInstance();

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> {
            layeredDrawer.addLayer(renderer);
        });

        if (FairUtilsConfig.get().taskTrackingEnabled) {
            enableModule();
        }

//        if (taskManager.getAllTasks().isEmpty()) {
//            createDemoTask();
//        }
    }

    /**
     * Creates a demo task for testing
     */
    private void createDemoTask() {
        Task demoTask = new Task("Mining Expedition", "Collect resources from a mining trip");

        // Add some objectives
        demoTask.addObjective(new ItemCollectionObjective("Collect Iron", Items.RAW_IRON, 10));
        demoTask.addObjective(new ItemCollectionObjective("Collect Gold", Items.RAW_GOLD, 5));
        demoTask.addObjective(new ItemCollectionObjective("Collect Diamond", Items.DIAMOND, 3));

        taskManager.addTask(demoTask);
    }

    private void onClientTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null)
            return;

        // Update all active tasks
        taskManager.tick();
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
        // Any specific actions when enabled
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        // Any specific actions when disabled
    }

    @Override
    public String getModuleName() {
        return MODULE_CATEGORY;
    }

    @Override
    public String getModuleId() {
        return "task_tracking";
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}