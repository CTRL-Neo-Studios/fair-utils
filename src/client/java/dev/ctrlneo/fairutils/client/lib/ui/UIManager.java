package dev.ctrlneo.fairutils.client.lib.ui;

import dev.ctrlneo.fairutils.client.lib.ui.components.Container;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Manages UI screens and components
 */
public class UIManager {
    private static final UIManager INSTANCE = new UIManager();

    private UIManager() {
    }

    /**
     * Get the UI manager instance
     * 
     * @return The UI manager instance
     */
    public static UIManager getInstance() {
        return INSTANCE;
    }

    /**
     * Open a screen with the given container as the root component
     * 
     * @param title         The screen title
     * @param rootComponent The root component
     */
    public void openScreen(Text title, Container rootComponent) {
        MinecraftClient.getInstance().setScreen(new ComponentScreen(title, rootComponent));
    }

    /**
     * Open a screen with the given container as the root component
     * 
     * @param title         The screen title
     * @param rootComponent The root component
     */
    public void openScreen(String title, Container rootComponent) {
        openScreen(Text.literal(title), rootComponent);
    }

    /**
     * A screen that wraps a component
     */
    public static class ComponentScreen extends Screen {
        private final Container rootComponent;

        /**
         * Create a new component screen
         * 
         * @param title         The screen title
         * @param rootComponent The root component
         */
        public ComponentScreen(Text title, Container rootComponent) {
            super(title);
            this.rootComponent = rootComponent;
        }

        @Override
        protected void init() {
            super.init();

            // Resize root component to fill screen
            rootComponent.setSize(width, height);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
            // Draw background
            renderBackground(context, mouseX, mouseY, delta);

            // Draw root component
            rootComponent.render(context, 0, 0, mouseX, mouseY, delta);

            // Draw tooltip if needed
            /*
             * If you need to render tooltips for items or components,
             * you should call the appropriate methods here after rendering
             * the main components, e.g.:
             * 
             * itemRenderer.drawTooltip(context, mouseX, mouseY);
             * 
             * or
             * 
             * Tooltip.drawTooltipIfHovered(context, component, tooltip, mouseX, mouseY);
             */

        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (rootComponent.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (rootComponent.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (rootComponent.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            if (rootComponent.mouseScrolled(mouseX, mouseY, verticalAmount)) {
                return true;
            }
            return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (rootComponent.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            if (rootComponent.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
            return super.keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            if (rootComponent.charTyped(chr, modifiers)) {
                return true;
            }
            return super.charTyped(chr, modifiers);
        }
    }
}