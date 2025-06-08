package dev.ctrlneo.fairutils.client.lib.ui;

import net.minecraft.client.gui.DrawContext;

/**
 * Base interface for all UI components
 */
public interface Component {
    /**
     * Render the component
     * 
     * @param context The draw context
     * @param x       The x position
     * @param y       The y position
     * @param mouseX  The mouse x position
     * @param mouseY  The mouse y position
     * @param delta   The time since last frame
     */
    void render(DrawContext context, int x, int y, int mouseX, int mouseY, float delta);

    /**
     * Handle mouse click
     * 
     * @param mouseX The mouse x position
     * @param mouseY The mouse y position
     * @param button The mouse button
     * @return True if the click was handled
     */
    boolean mouseClicked(double mouseX, double mouseY, int button);

    /**
     * Handle mouse release
     * 
     * @param mouseX The mouse x position
     * @param mouseY The mouse y position
     * @param button The mouse button
     * @return True if the release was handled
     */
    boolean mouseReleased(double mouseX, double mouseY, int button);

    /**
     * Handle mouse drag
     * 
     * @param mouseX The mouse x position
     * @param mouseY The mouse y position
     * @param button The mouse button
     * @param deltaX The change in x
     * @param deltaY The change in y
     * @return True if the drag was handled
     */
    boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);

    /**
     * Handle mouse scroll
     * 
     * @param mouseX The mouse x position
     * @param mouseY The mouse y position
     * @param amount The scroll amount
     * @return True if the scroll was handled
     */
    boolean mouseScrolled(double mouseX, double mouseY, double amount);

    /**
     * Handle key press
     * 
     * @param keyCode   The key code
     * @param scanCode  The scan code
     * @param modifiers The modifiers
     * @return True if the key press was handled
     */
    boolean keyPressed(int keyCode, int scanCode, int modifiers);

    /**
     * Handle key release
     * 
     * @param keyCode   The key code
     * @param scanCode  The scan code
     * @param modifiers The modifiers
     * @return True if the key release was handled
     */
    boolean keyReleased(int keyCode, int scanCode, int modifiers);

    /**
     * Handle character typed
     * 
     * @param chr       The character
     * @param modifiers The modifiers
     * @return True if the character typed was handled
     */
    boolean charTyped(char chr, int modifiers);

    /**
     * Get the width of the component
     * 
     * @return The width
     */
    int getWidth();

    /**
     * Get the height of the component
     * 
     * @return The height
     */
    int getHeight();

    /**
     * Get the style of the component
     * 
     * @return The style
     */
    Style getStyle();

    /**
     * Set the style of the component
     * 
     * @param style The style
     * @return This component for chaining
     */
    Component setStyle(Style style);

    /**
     * Check if the mouse is over the component
     * 
     * @param mouseX The mouse x position
     * @param mouseY The mouse y position
     * @return True if the mouse is over the component
     */
    boolean isMouseOver(double mouseX, double mouseY);
}