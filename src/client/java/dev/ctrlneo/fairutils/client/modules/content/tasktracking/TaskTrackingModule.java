package dev.ctrlneo.fairutils.client.modules.content.tasktracking;

import dev.ctrlneo.fairutils.client.config.FairUtilsConfig;
import dev.ctrlneo.fairutils.client.lib.ui.UIManager;
import dev.ctrlneo.fairutils.client.modules.UtilityModule;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.gui.TaskOverlayRenderLayer;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.gui.screens.TaskTrackingScreen;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.objectives.ItemCollectionObjective;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.utility.TaskManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;

public class TaskTrackingModule extends UtilityModule {
    public static final String MODULE_CATEGORY = "Task Tracking";
    public static final TaskManager TASK_MANAGER = new TaskManager();
    public static final KeyBinding OPEN_TASK_TRACKING_GUI = new KeyBinding("fairutils", InputUtil.GLFW_KEY_0, MODULE_CATEGORY);
    private final TaskOverlayRenderLayer renderer = new TaskOverlayRenderLayer(TASK_MANAGER);
    private MinecraftClient mc;

    @Override
    public void initialize() {
        super.initialize();
        mc = MinecraftClient.getInstance();

        // Register tick event for updating tasks
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

        // Register renderer for HUD overlay
        HudLayerRegistrationCallback.EVENT.register(layeredDrawerWrapper -> {
            layeredDrawerWrapper.addLayer(renderer);
        });

        // Enable module based on config
        if (FairUtilsConfig.get().taskTrackingEnabled) {
            enableModule();
        }
    }

    /**
     * Called each tick to update the tasks
     */
    private void onClientTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null)
            return;

        if (OPEN_TASK_TRACKING_GUI.wasPressed()) {
            UIManager.to(new TaskTrackingScreen());
        }

        TASK_MANAGER.tick();
    }

    /**
     * Create a demo task for testing
     */
    private void createDemoTask() {
        // Create a simple collection task
        Task demoTask = new Task("Collect Resources", "Gather materials for crafting");

        // Add objectives
        demoTask.addObjective(new ItemCollectionObjective("Collect Iron", Items.RAW_IRON, 16));
        demoTask.addObjective(new ItemCollectionObjective("Collect Coal", Items.COAL, 32));

        // Add the task to the manager
        TASK_MANAGER.addTask(demoTask);
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
        // nothing to do
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        // nothing to do
    }

    @Override
    public String getModuleName() {
        return MODULE_CATEGORY;
    }

    @Override
    public String getModuleId() {
        return "task_tracking";
    }

    /**
     * Get the task manager instance
     */
    public TaskManager getTaskManager() {
        return TASK_MANAGER;
    }
}