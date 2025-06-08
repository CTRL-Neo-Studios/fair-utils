package dev.ctrlneo.fairutils.client.lib.ui;

import net.minecraft.client.gui.DrawContext;

/**
 * Base implementation for UI components
 */
public abstract class AbstractComponent implements Component {
    protected Style style = new Style();
    protected int width;
    protected int height;
    protected boolean visible = true;
    protected boolean enabled = true;

    @Override
    public void render(DrawContext context, int x, int y, int mouseX, int mouseY, float delta) {
        if (!visible)
            return;

        // Apply styles
        int paddingLeft = style.getInt("paddingLeft", style.getInt("padding", 0));
        int paddingTop = style.getInt("paddingTop", style.getInt("padding", 0));
        int paddingRight = style.getInt("paddingRight", style.getInt("padding", 0));
        int paddingBottom = style.getInt("paddingBottom", style.getInt("padding", 0));

        // Calculate content area
        int contentX = x + paddingLeft;
        int contentY = y + paddingTop;
        int contentWidth = width - paddingLeft - paddingRight;
        int contentHeight = height - paddingTop - paddingBottom;

        // Save the current matrix state
        context.getMatrices().push();

        // Apply animations if any
        applyAnimations(context, x, y, delta);

        // Render the component content
        renderContent(context, contentX, contentY, contentWidth, contentHeight, mouseX, mouseY, delta);

        // Restore the matrix state
        context.getMatrices().pop();
    }

    /**
     * Apply animations to the component
     * Override in subclasses to implement animations
     */
    protected void applyAnimations(DrawContext context, int x, int y, float delta) {
        // Default implementation does nothing
    }

    /**
     * Render the content of the component
     * 
     * @param context The draw context
     * @param x       The x position of the content area
     * @param y       The y position of the content area
     * @param width   The width of the content area
     * @param height  The height of the content area
     * @param mouseX  The mouse x position
     * @param mouseY  The mouse y position
     * @param delta   The time since last frame
     */
    protected abstract void renderContent(DrawContext context, int x, int y, int width, int height, int mouseX,
            int mouseY, float delta);

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return isMouseOver(mouseX, mouseY) && enabled;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Style getStyle() {
        return style;
    }

    @Override
    public Component setStyle(Style style) {
        this.style = style;
        return this;
    }

    /**
     * Set the size of the component
     * 
     * @param width  The width
     * @param height The height
     * @return This component for chaining
     */
    public AbstractComponent setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Set the visibility of the component
     * 
     * @param visible True if the component should be visible
     * @return This component for chaining
     */
    public AbstractComponent setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    /**
     * Set the enabled state of the component
     * 
     * @param enabled True if the component should be enabled
     * @return This component for chaining
     */
    public AbstractComponent setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Check if the mouse is over the component
     * 
     * @param mouseX The mouse x position
     * @param mouseY The mouse y position
     * @return True if the mouse is over the component
     */
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= 0 && mouseX < width && mouseY >= 0 && mouseY < height;
    }
}