package dev.ctrlneo.fairutils.client.lib.ui.components;

import dev.ctrlneo.fairutils.client.lib.ui.AbstractComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * A component for rendering Minecraft items
 */
public class ItemRenderer extends AbstractComponent {
    private ItemStack itemStack;
    private boolean showTooltip = true;
    private boolean renderCount = true;
    private float scale = 1.0f;
    private int overlayColor = 0;

    /**
     * Create a new item renderer
     */
    public ItemRenderer() {
        this.width = 16;
        this.height = 16;
    }

    /**
     * Create a new item renderer with the given item stack
     * 
     * @param itemStack The item stack to render
     */
    public ItemRenderer(ItemStack itemStack) {
        this();
        this.itemStack = itemStack;
    }

    /**
     * Set the item stack to render
     * 
     * @param itemStack The item stack to render
     * @return This item renderer for chaining
     */
    public ItemRenderer setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    /**
     * Set whether to show the tooltip when hovering
     * 
     * @param showTooltip Whether to show the tooltip
     * @return This item renderer for chaining
     */
    public ItemRenderer setShowTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
        return this;
    }

    /**
     * Set whether to render the item count
     * 
     * @param renderCount Whether to render the item count
     * @return This item renderer for chaining
     */
    public ItemRenderer setRenderCount(boolean renderCount) {
        this.renderCount = renderCount;
        return this;
    }

    /**
     * Set the render scale
     * 
     * @param scale The render scale
     * @return This item renderer for chaining
     */
    public ItemRenderer setScale(float scale) {
        this.scale = scale;
        int scaledSize = Math.round(16 * scale);
        this.width = scaledSize;
        this.height = scaledSize;
        return this;
    }

    /**
     * Set an overlay color for the item
     * 
     * @param overlayColor The overlay color (0 for no overlay)
     * @return This item renderer for chaining
     */
    public ItemRenderer setOverlayColor(int overlayColor) {
        this.overlayColor = overlayColor;
        return this;
    }

    @Override
    protected void renderContent(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY,
            float delta) {
        if (itemStack == null || itemStack.isEmpty())
            return;

        // Save matrices
        context.getMatrices().push();

        // Apply scale
        if (scale != 1.0f) {
            float centerX = x + width / 2f;
            float centerY = y + height / 2f;

            context.getMatrices().translate(centerX, centerY, 0);
            context.getMatrices().scale(scale, scale, 1.0f);
            context.getMatrices().translate(-8, -8, 0); // Center around 16x16 item

            // Adjust x and y for scaled rendering
            x = 0;
            y = 0;
        }

        // Render item
        net.minecraft.client.render.item.ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        context.drawItemWithoutEntity(itemStack, x, y);

        // Render overlay if needed
        if (overlayColor != 0) {
            context.fill(x, y, x + 16, y + 16, overlayColor);
        }

        // Render count if needed
        if (renderCount && itemStack.getCount() > 1) {
            // Draw the count text manually
            String countText = String.valueOf(itemStack.getCount());
            int countX = x + 17 - MinecraftClient.getInstance().textRenderer.getWidth(countText);
            int countY = y + 9;
            context.drawText(MinecraftClient.getInstance().textRenderer, countText, countX, countY, 0xFFFFFFFF, true);
        }

        // Restore matrices
        context.getMatrices().pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Just handle hover, no special click behavior
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Draw the tooltip for this item
     * 
     * @param context The draw context
     * @param mouseX  The mouse x position
     * @param mouseY  The mouse y position
     */
    public void drawTooltip(DrawContext context, int mouseX, int mouseY) {
        if (showTooltip && itemStack != null && !itemStack.isEmpty() && isMouseOver(mouseX, mouseY)) {
            context.drawItemTooltip(MinecraftClient.getInstance().textRenderer, itemStack, mouseX, mouseY);
        }
    }

    /**
     * Get the item stack being rendered
     * 
     * @return The item stack
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Get the item name
     * 
     * @return The item name
     */
    public Text getName() {
        return itemStack != null ? itemStack.getName() : Text.empty();
    }
}