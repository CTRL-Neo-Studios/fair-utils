package dev.ctrlneo.fairutils.client.modules.content.tasktracking.gui.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;

public class TaskTrackingScreen extends Screen {

    private MinecraftClient client;

    protected TaskTrackingScreen() {
        super(Text.literal("Task Tracker"));
        client = MinecraftClient.getInstance();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

//        context.fill(RenderLayer.getGuiOverlay(), 0, 0, client.getWindow().getWidth(),);
        context.getMatrices().push();
    }
}
