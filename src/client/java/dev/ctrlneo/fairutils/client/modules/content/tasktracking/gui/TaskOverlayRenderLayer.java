package dev.ctrlneo.fairutils.client.modules.content.tasktracking.gui;

import dev.ctrlneo.fairutils.client.config.FairUtilsConfig;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.Task;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.TaskObjective;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.TaskTrackingModule;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.utility.TaskOverlayPosition;
import dev.ctrlneo.fairutils.client.utility.Reference;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Renders tasks on the HUD
 */
public class TaskOverlayRenderLayer implements IdentifiedLayer {
    private static final int BACKGROUND_COLOR = 0x80000000; // Semi-transparent black
    private static final int PROGRESS_BAR_COLOR = 0xFF3FB53F; // Green
    private static final int TITLE_COLOR = 0xFFFFFF; // White
    private static final int DESCRIPTION_COLOR = 0xCCCCCC; // Light gray
    private static final int OBJECTIVE_COLOR = 0xAAAAAA; // Gray
    private static final int COMPLETED_COLOR = 0x55FF55; // Bright green

    private MinecraftClient client;

    public TaskOverlayRenderLayer() {
    }

    @Override
    public Identifier id() {
        return Reference.of("task_tracking_hud");
    }

    @Override
    public void render(DrawContext context, RenderTickCounter tickCounter) {
        if(client == null) client = MinecraftClient.getInstance();

        if (client.player == null || client.options.hudHidden || TaskTrackingModule.storage().isEmpty())
            return;

        if (!FairUtilsConfig.get().taskTrackingEnabled || !FairUtilsConfig.get().showTaskTrackingOverlay) {
            return;
        }

        List<Task> visibleTasks = TaskTrackingModule.storage().get().getVisibleTasks();
        if (visibleTasks.isEmpty())
            return;

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        TextRenderer textRenderer = client.textRenderer;

        int x = width - 210; // Right side of screen with padding
        int y = 40; // Top of screen with padding

        // Determine display position from config
        TaskOverlayPosition position = FairUtilsConfig.get().taskTrackingPosition;
        switch (position) {
            case TOP_LEFT:
                x = 10;
                y = 40;
                break;
            case TOP_RIGHT:
                x = width - 210;
                y = 40;
                break;
            case BOTTOM_LEFT:
                x = 10;
                y = height - 150;
                break;
            case BOTTOM_RIGHT:
                x = width - 210;
                y = height - 150;
                break;
        }

        // Limit tasks shown based on config
        int maxTasksToShow = Math.min(visibleTasks.size(), FairUtilsConfig.get().maxVisibleTasks);

        // Draw header
        String headerText = "Tasks";
        context.drawText(textRenderer, headerText, x + 5, y, TITLE_COLOR, true);

        y += 15;

        // Draw tasks
        for (int i = 0; i < maxTasksToShow; i++) {
            Task task = visibleTasks.get(i);

            // Draw task background
            int taskHeight = 15 + (task.getObjectives().size() * 10);
            context.fill(x, y, x + 200, y + taskHeight, BACKGROUND_COLOR);

            // Draw task title
            String titleText = task.getTitle();
            int titleColor = task.isCompleted() ? COMPLETED_COLOR : TITLE_COLOR;
            context.drawText(textRenderer, titleText, x + 5, y + 2, titleColor, false);

            // Draw progress bar
            int progressWidth = (int) (190 * task.getProgress());
            context.fill(x + 5, y + 12, x + 5 + progressWidth, y + 14, PROGRESS_BAR_COLOR);

            // Draw objectives
            int objY = y + 17;
            for (TaskObjective objective : task.getObjectives()) {
                String objectiveText = objective.getDescription() + ": ";
                String progressText = objective.getProgressText().getString();

                int objColor = objective.isCompleted() ? COMPLETED_COLOR : OBJECTIVE_COLOR;

                context.drawText(textRenderer, objectiveText, x + 10, objY, objColor, false);
                context.drawText(textRenderer, progressText, x + 190 - textRenderer.getWidth(progressText), objY,
                        objColor, false);

                objY += 10;
            }

            y += taskHeight + 5; // Add spacing between tasks
        }

        // If there are more tasks than shown, indicate with "..."
        if (visibleTasks.size() > maxTasksToShow) {
            String moreText = "... and " + (visibleTasks.size() - maxTasksToShow) + " more";
            context.drawText(textRenderer, moreText, x + 5, y, DESCRIPTION_COLOR, false);
        }
    }
}