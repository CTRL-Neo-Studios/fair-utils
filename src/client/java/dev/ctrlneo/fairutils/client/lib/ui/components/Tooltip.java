package dev.ctrlneo.fairutils.client.lib.ui.components;

import dev.ctrlneo.fairutils.client.lib.ui.Component;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A tooltip component that displays when hovering over another component
 */
public class Tooltip {
    private final List<Text> lines = new ArrayList<>();
    private int backgroundColor = 0xF0100010;
    private int borderColorStart = 0x505000FF;
    private int borderColorEnd = 0x5028007F;
    private int padding = 4;
    private int maxWidth = 200;

    /**
     * Create a new tooltip with the given text
     * 
     * @param text The tooltip text
     */
    public Tooltip(Text text) {
        this.lines.add(text);
    }

    /**
     * Create a new tooltip with the given text
     * 
     * @param text The tooltip text
     */
    public Tooltip(String text) {
        this(Text.literal(text));
    }

    /**
     * Create a new tooltip with the given lines
     * 
     * @param lines The tooltip lines
     */
    public Tooltip(Text... lines) {
        this.lines.addAll(Arrays.asList(lines));
    }

    /**
     * Create a new tooltip with the given lines
     * 
     * @param lines The tooltip lines
     */
    public Tooltip(String... lines) {
        for (String line : lines) {
            this.lines.add(Text.literal(line));
        }
    }

    /**
     * Add a line to the tooltip
     * 
     * @param text The line to add
     * @return This tooltip for chaining
     */
    public Tooltip addLine(Text text) {
        lines.add(text);
        return this;
    }

    /**
     * Add a line to the tooltip
     * 
     * @param text The line to add
     * @return This tooltip for chaining
     */
    public Tooltip addLine(String text) {
        return addLine(Text.literal(text));
    }

    /**
     * Set the background color
     * 
     * @param backgroundColor The background color
     * @return This tooltip for chaining
     */
    public Tooltip setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * Set the border colors
     * 
     * @param borderColorStart The start color
     * @param borderColorEnd   The end color
     * @return This tooltip for chaining
     */
    public Tooltip setBorderColors(int borderColorStart, int borderColorEnd) {
        this.borderColorStart = borderColorStart;
        this.borderColorEnd = borderColorEnd;
        return this;
    }

    /**
     * Set the padding
     * 
     * @param padding The padding
     * @return This tooltip for chaining
     */
    public Tooltip setPadding(int padding) {
        this.padding = padding;
        return this;
    }

    /**
     * Set the maximum width
     * 
     * @param maxWidth The maximum width
     * @return This tooltip for chaining
     */
    public Tooltip setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    /**
     * Render the tooltip
     * 
     * @param context The draw context
     * @param x       The x position
     * @param y       The y position
     */
    public void render(DrawContext context, int x, int y) {
        if (lines.isEmpty())
            return;

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        // Split long lines
        List<Text> wrappedLines = new ArrayList<>();
        for (Text line : lines) {
            String text = line.getString();
            int lineWidth = textRenderer.getWidth(text);

            if (lineWidth <= maxWidth) {
                wrappedLines.add(line);
            } else {
                // Simple word wrap
                String[] words = text.split(" ");
                StringBuilder currentLine = new StringBuilder();

                for (String word : words) {
                    String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;

                    if (textRenderer.getWidth(testLine) <= maxWidth) {
                        if (currentLine.length() > 0) {
                            currentLine.append(" ");
                        }
                        currentLine.append(word);
                    } else {
                        if (currentLine.length() > 0) {
                            wrappedLines.add(Text.literal(currentLine.toString()));
                            currentLine = new StringBuilder(word);
                        } else {
                            // Word is too long, split it
                            wrappedLines.add(Text.literal(word));
                        }
                    }
                }

                if (currentLine.length() > 0) {
                    wrappedLines.add(Text.literal(currentLine.toString()));
                }
            }
        }

        // Calculate tooltip size
        int tooltipWidth = 0;
        for (Text line : wrappedLines) {
            int lineWidth = textRenderer.getWidth(line);
            tooltipWidth = Math.max(tooltipWidth, lineWidth);
        }

        int tooltipHeight = wrappedLines.size() * 10;
        if (wrappedLines.size() > 1) {
            tooltipHeight += 2; // Gap between lines
        }

        // Adjust position if the tooltip would go off screen
        int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

        if (x + tooltipWidth + 6 > screenWidth) {
            x = screenWidth - tooltipWidth - 6;
        }

        if (y + tooltipHeight + 6 > screenHeight) {
            y = screenHeight - tooltipHeight - 6;
        }

        // Draw background
        int left = x - 3;
        int top = y - 3;
        int right = x + tooltipWidth + 3;
        int bottom = y + tooltipHeight + 3;

        context.fill(left, top, right, bottom, backgroundColor);
        context.fill(left, top, left + 1, bottom, borderColorStart);
        context.fill(right - 1, top, right, bottom, borderColorEnd);
        context.fill(left, top, right, top + 1, borderColorStart);
        context.fill(left, bottom - 1, right, bottom, borderColorEnd);

        // Draw text
        int textY = y;
        for (Text line : wrappedLines) {
            context.drawText(textRenderer, line, x, textY, 0xFFFFFFFF, false);
            textY += 10;
        }
    }

    /**
     * Draw a tooltip for a component if the mouse is over it
     * 
     * @param context   The draw context
     * @param component The component
     * @param tooltip   The tooltip
     * @param mouseX    The mouse x position
     * @param mouseY    The mouse y position
     */
    public static void drawTooltipIfHovered(DrawContext context, Component component, Tooltip tooltip, int mouseX,
            int mouseY) {
        if (component.isMouseOver(mouseX, mouseY)) {
            tooltip.render(context, mouseX + 12, mouseY);
        }
    }
}