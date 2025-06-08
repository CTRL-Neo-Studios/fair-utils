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
        private int lastMouseX = -1; // Track mouse for components
        private int lastMouseY = -1; // Track mouse for components

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
            // Update last known mouse position
            lastMouseX = mouseX;
            lastMouseY = mouseY;

            // Draw the screen background first
            super.render(context, mouseX, mouseY, delta);
            renderBackground(context, mouseX, mouseY, delta);

            // Then draw the root component
            rootComponent.render(context, 0, 0, mouseX, mouseY, delta);

            // Draw UI elements (like tooltip) if needed
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

            // Draw the vanilla UI elements last (like title)
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            // Let the component handle the click first
            if (rootComponent.isMouseOver(mouseX, mouseY)) {
                if (rootComponent.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
            // Then let the screen handle it
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            // Let the component handle the release first
            if (rootComponent.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
            // Then let the screen handle it
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            // Let the component handle the drag first
            if (rootComponent.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
            // Then let the screen handle it
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            // Let the component handle the scroll first
            if (rootComponent.mouseScrolled(mouseX, mouseY, verticalAmount)) {
                return true;
            }
            // Then let the screen handle it
            return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            // Let the component handle the key press first
            if (rootComponent.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            // Then let the screen handle it
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            // Let the component handle the key release first
            if (rootComponent.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
            // Then let the screen handle it
            return super.keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            // Let the component handle the char typed first
            if (rootComponent.charTyped(chr, modifiers)) {
                return true;
            }
            // Then let the screen handle it
            return super.charTyped(chr, modifiers);
        }
    }
}