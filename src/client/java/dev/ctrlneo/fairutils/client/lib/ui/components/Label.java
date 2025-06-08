package dev.ctrlneo.fairutils.client.lib.ui.components;

import dev.ctrlneo.fairutils.client.lib.ui.AbstractComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * A label component for displaying text
 */
public class Label extends AbstractComponent {
    private Text text;
    private int color = 0xFFFFFFFF;
    private boolean shadow = false;
    private TextAlignment alignment = TextAlignment.LEFT;

    /**
     * Create a new label with the given text
     * 
     * @param text The label text
     */
    public Label(Text text) {
        this.text = text;
        updateSize();
    }

    /**
     * Create a new label with the given text
     * 
     * @param text The label text
     */
    public Label(String text) {
        this(Text.literal(text));
    }

    /**
     * Set the label text
     * 
     * @param text The label text
     * @return This label for chaining
     */
    public Label setText(Text text) {
        this.text = text;
        updateSize();
        return this;
    }

    /**
     * Set the label text
     * 
     * @param text The label text
     * @return This label for chaining
     */
    public Label setText(String text) {
        return setText(Text.literal(text));
    }

    /**
     * Set the text color
     * 
     * @param color The text color
     * @return This label for chaining
     */
    public Label setColor(int color) {
        this.color = color;
        return this;
    }

    /**
     * Set whether to draw text with shadow
     * 
     * @param shadow Whether to draw text with shadow
     * @return This label for chaining
     */
    public Label setShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    /**
     * Set the text alignment
     * 
     * @param alignment The text alignment
     * @return This label for chaining
     */
    public Label setAlignment(TextAlignment alignment) {
        this.alignment = alignment;
        return this;
    }

    /**
     * Update the size of the label based on the text
     */
    private void updateSize() {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        this.width = textRenderer.getWidth(text);
        this.height = 10; // Text height is about 8-9px, rounded up to 10
    }

    @Override
    protected void renderContent(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY,
            float delta) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int textWidth = textRenderer.getWidth(text);

        // Calculate x position based on alignment
        int textX = x;
        switch (alignment) {
            case CENTER:
                textX = x + (width - textWidth) / 2;
                break;
            case RIGHT:
                textX = x + width - textWidth;
                break;
            default: // LEFT
                break;
        }

        // Draw text
        context.drawText(textRenderer, text, textX, y + (height - 8) / 2, color, shadow);
    }

    /**
     * Text alignment options
     */
    public enum TextAlignment {
        LEFT, CENTER, RIGHT
    }
}