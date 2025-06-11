package dev.ctrlneo.fairutils.client.modules.content.tasktracking;

import dev.ctrlneo.fairutils.client.config.FairUtilsConfig;
import dev.ctrlneo.fairutils.client.lib.ui.UIManager;
import dev.ctrlneo.fairutils.client.modules.UtilityModule;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.event.TaskProgressChangedEvent;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.gui.TaskOverlayRenderLayer;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.gui.screens.TaskTrackingScreen;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.objectives.ItemCollectionObjective;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.utility.TaskStorage;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.util.WorldSavePath;

import java.util.Objects;
import java.util.Optional;

public class TaskTrackingModule extends UtilityModule {
    public static final String MODULE_CATEGORY = "Task Tracking";
    private static TaskStorage TASK_STORAGE;
    public static final KeyBinding OPEN_TASK_TRACKING_GUI = new KeyBinding("fairutils", InputUtil.GLFW_KEY_0, MODULE_CATEGORY);

    private MinecraftClient mc;

    public static Optional<TaskStorage> storage() {
        if (TASK_STORAGE == null) return Optional.empty();
        return Optional.of(TASK_STORAGE);
    }

    @Override
    public void initialize() {
        super.initialize();
        mc = MinecraftClient.getInstance();

        // Register tick event for updating tasks
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

        // Register renderer for HUD overlay
        HudLayerRegistrationCallback.EVENT.register(layeredDrawerWrapper -> {
            layeredDrawerWrapper.addLayer(new TaskOverlayRenderLayer());
        });

        // Enable module based on config
        if (FairUtilsConfig.get().taskTrackingEnabled) {
            enableModule();
        }

        TaskProgressChangedEvent.EVENT.register(uuid -> {

        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            TASK_STORAGE.saveTasks();
        });

        ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, packetSender, minecraftClient) -> {
            String worldIdentifier;
            boolean isMultiplayer = minecraftClient.getServer() == null;
            if (isMultiplayer) worldIdentifier = Objects.requireNonNull(clientPlayNetworkHandler.getServerInfo()).address;
            else worldIdentifier = minecraftClient.getServer().getSavePath(WorldSavePath.ROOT).getParent().getFileName().toString();

            TASK_STORAGE = new TaskStorage(worldIdentifier, isMultiplayer);
        });
    }

    /**
     * Called each tick to update the tasks
     */
    private void onClientTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null)
            return;

        if (OPEN_TASK_TRACKING_GUI.wasPressed() && storage().isPresent()) {
            UIManager.to(new TaskTrackingScreen());
        }

        if (storage().isPresent())
            storage().get().tick();
    }

    /**
     * Create a demo task for testing
     */
    private void createDemoTask() {
        // Create a simple collection task
        Task demoTask = new Task("Collect Resources", "Gather materials for crafting");

        // Add objectives
        demoTask.addObjective(new ItemCollectionObjective(demoTask.getId(), "Collect Iron", Items.RAW_IRON, 16));
        demoTask.addObjective(new ItemCollectionObjective(demoTask.getId(), "Collect Coal", Items.COAL, 32));

        // Add the task to the manager
        TASK_STORAGE.addTask(demoTask);
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

}