package dev.ctrlneo.fairutils.client.lib.ui.components;

import dev.ctrlneo.fairutils.client.lib.ui.AbstractComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.function.Consumer;

/**
 * A button component with minimalist contrast style
 */
public class Button extends AbstractComponent {
    private Text text;
    private boolean hovered;
    private boolean pressed;
    private Consumer<Button> onClick;

    /**
     * Create a new button with the given text
     * 
     * @param text The button text
     */
    public Button(Text text) {
        this.text = text;
        this.width = MinecraftClient.getInstance().textRenderer.getWidth(text) + 16;
        this.height = 20;
    }

    /**
     * Create a new button with the given text
     * 
     * @param text The button text
     */
    public Button(String text) {
        this(Text.literal(text));
    }

    /**
     * Set the button text
     * 
     * @param text The button text
     * @return This button for chaining
     */
    public Button setText(Text text) {
        this.text = text;
        this.width = MinecraftClient.getInstance().textRenderer.getWidth(text) + 16;
        return this;
    }

    /**
     * Set the button text
     * 
     * @param text The button text
     * @return This button for chaining
     */
    public Button setText(String text) {
        return setText(Text.literal(text));
    }

    /**
     * Set the click handler
     * 
     * @param onClick The click handler
     * @return This button for chaining
     */
    public Button onClick(Consumer<Button> onClick) {
        this.onClick = onClick;
        return this;
    }

    @Override
    protected void renderContent(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY,
            float delta) {
        this.hovered = mouseX >= 0 && mouseY >= 0 && mouseX < width && mouseY < height;

        // Render button background
        int backgroundColor, textColor;
        if (!enabled) {
            backgroundColor = 0xFF555555;
            textColor = 0xFFAAAAAA;
        } else if (pressed) {
            backgroundColor = 0xFF000000;
            textColor = 0xFFFFFFFF;
        } else if (hovered) {
            backgroundColor = 0xFF000000;
            textColor = 0xFFFFFFFF;
        } else {
            backgroundColor = 0xFFFFFFFF;
            textColor = 0xFF000000;
        }

        // Draw background
        context.fill(x, y, x + width, y + height, backgroundColor);

        // Draw outline when hovered
        if (hovered && enabled && !pressed) {
            context.drawBorder(x, y, width, height, 0xFFFFFFFF);
        }

        // Draw text
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int textWidth = textRenderer.getWidth(text);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - 8) / 2;

        context.drawText(textRenderer, text, textX, textY, textColor, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button) && button == 0) {
            playClickSound();
            pressed = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (pressed && button == 0) {
            pressed = false;

            // Check if still hovered
            if (mouseX >= 0 && mouseX < width && mouseY >= 0 && mouseY < height) {
                if (onClick != null) {
                    onClick.accept(this);
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Play the button click sound
     */
    private void playClickSound() {
        MinecraftClient.getInstance().getSoundManager().play(
                PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}