package dev.ctrlneo.fairutils.client.lib.ui.components;

import dev.ctrlneo.fairutils.client.lib.ui.AbstractComponent;
import dev.ctrlneo.fairutils.client.lib.ui.Component;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

/**
 * A scroll panel component that can scroll its content vertically
 */
public class ScrollPanel extends AbstractComponent {
    private Component content;
    private int scrollPosition = 0;
    private int maxScrollPosition = 0;
    private boolean dragging = false;
    private int lastMouseY;
    private int scrollbarWidth = 6;

    /**
     * Create a new scroll panel
     */
    public ScrollPanel() {
        this.width = 200;
        this.height = 100;
    }

    /**
     * Set the content component
     * 
     * @param content The content component
     * @return This scroll panel for chaining
     */
    public ScrollPanel setContent(Component content) {
        this.content = content;
        updateMaxScrollPosition();
        return this;
    }

    /**
     * Update the maximum scroll position based on content height
     */
    private void updateMaxScrollPosition() {
        if (content != null) {
            maxScrollPosition = Math.max(0, content.getHeight() - height);
        } else {
            maxScrollPosition = 0;
        }
        scrollPosition = MathHelper.clamp(scrollPosition, 0, maxScrollPosition);
    }

    /**
     * Set the scroll position
     * 
     * @param scrollPosition The scroll position
     * @return This scroll panel for chaining
     */
    public ScrollPanel setScrollPosition(int scrollPosition) {
        this.scrollPosition = MathHelper.clamp(scrollPosition, 0, maxScrollPosition);
        return this;
    }

    /**
     * Get the scroll position
     * 
     * @return The scroll position
     */
    public int getScrollPosition() {
        return scrollPosition;
    }

    /**
     * Set the scrollbar width
     * 
     * @param scrollbarWidth The scrollbar width
     * @return This scroll panel for chaining
     */
    public ScrollPanel setScrollbarWidth(int scrollbarWidth) {
        this.scrollbarWidth = scrollbarWidth;
        return this;
    }

    @Override
    protected void renderContent(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY,
            float delta) {
        // Update max scroll position in case content changed
        updateMaxScrollPosition();

        // Set up scissor to clip content to the panel area
        double scale = MinecraftClient.getInstance().getWindow().getScaleFactor();

        int scaledX = (int) (x * scale);
        int scaledY = (int) (y * scale);
        int scaledWidth = (int) (width * scale);
        int scaledHeight = (int) (height * scale);

        // Enable scissor
        context.enableScissor(scaledX, scaledY, scaledX + scaledWidth, scaledY + scaledHeight);

        // Render content at scrolled position
        if (content != null) {
            content.render(context, x, y - scrollPosition, mouseX, mouseY + scrollPosition, delta);
        }

        // Disable scissor
        context.disableScissor();

        // Render scrollbar if needed
        if (maxScrollPosition > 0) {
            // Calculate scrollbar dimensions
            int scrollbarHeight = Math.max(20, height * height / (height + maxScrollPosition));
            int scrollbarY = y + (int) ((height - scrollbarHeight) * ((float) scrollPosition / maxScrollPosition));
            int scrollbarX = x + width - scrollbarWidth;

            // Draw scrollbar background
            context.fill(scrollbarX, y, scrollbarX + scrollbarWidth, y + height, 0x33FFFFFF);

            // Draw scrollbar
            boolean scrollbarHovered = mouseX >= scrollbarX && mouseX < scrollbarX + scrollbarWidth &&
                    mouseY >= scrollbarY && mouseY < scrollbarY + scrollbarHeight;
            int scrollbarColor = dragging ? 0xFFFFFFFF : (scrollbarHovered ? 0xDDFFFFFF : 0x99FFFFFF);
            context.fill(scrollbarX, scrollbarY, scrollbarX + scrollbarWidth, scrollbarY + scrollbarHeight,
                    scrollbarColor);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) {
            return false;
        }

        if (button == 0 && maxScrollPosition > 0) {
            // Check if clicked on scrollbar
            int scrollbarHeight = Math.max(20, height * height / (height + maxScrollPosition));
            int scrollbarY = (int) ((height - scrollbarHeight) * ((float) scrollPosition / maxScrollPosition));
            int scrollbarX = width - scrollbarWidth;

            if (mouseX >= scrollbarX && mouseX < scrollbarX + scrollbarWidth &&
                    mouseY >= scrollbarY && mouseY < scrollbarY + scrollbarHeight) {
                dragging = true;
                lastMouseY = (int) mouseY;
                return true;
            }
        }

        // Pass click to content if not on scrollbar and within visible area
        if (content != null && mouseX >= 0 && mouseX < width - scrollbarWidth) {
            return content.mouseClicked(mouseX, mouseY + scrollPosition, button);
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging && button == 0) {
            dragging = false;
            return true;
        }

        // Pass release to content
        if (content != null) {
            return content.mouseReleased(mouseX, mouseY + scrollPosition, button);
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging && button == 0) {
            // Calculate scroll amount
            int dy = (int) mouseY - lastMouseY;
            lastMouseY = (int) mouseY;

            if (dy != 0) {
                // Calculate scrollbar height
                int scrollbarHeight = Math.max(20, height * height / (height + maxScrollPosition));

                // Calculate scroll amount
                float scrollFactor = (float) maxScrollPosition / (height - scrollbarHeight);
                int scrollAmount = (int) (dy * scrollFactor);

                // Update scroll position
                setScrollPosition(scrollPosition + scrollAmount);

                return true;
            }
        }

        // Pass drag to content
        if (content != null) {
            return content.mouseDragged(mouseX, mouseY + scrollPosition, button, deltaX, deltaY);
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (mouseX >= 0 && mouseX < width && mouseY >= 0 && mouseY < height) {
            // Scroll by 20 pixels per wheel click
            int scrollAmount = (int) (-amount * 20);
            setScrollPosition(scrollPosition + scrollAmount);
            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (content != null) {
            return content.keyPressed(keyCode, scanCode, modifiers);
        }

        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (content != null) {
            return content.keyReleased(keyCode, scanCode, modifiers);
        }

        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (content != null) {
            return content.charTyped(chr, modifiers);
        }

        return false;
    }
}