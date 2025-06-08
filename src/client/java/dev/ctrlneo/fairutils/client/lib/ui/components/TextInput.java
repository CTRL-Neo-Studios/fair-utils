package dev.ctrlneo.fairutils.client.lib.ui.components;

import dev.ctrlneo.fairutils.client.lib.ui.AbstractComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A text input component
 */
public class TextInput extends AbstractComponent {
    private String text = "";
    private String placeholder = "";
    private int maxLength = 32;
    private int cursorPos = 0;
    private int selectionEnd = 0;
    private int displayOffset = 0;
    private boolean focused = false;
    private int focusedTicks = 0;
    private Consumer<String> onChange;
    private Predicate<String> validator;

    /**
     * Create a new text input
     */
    public TextInput() {
        this.width = 200;
        this.height = 20;
    }

    /**
     * Set the text
     * 
     * @param text The text
     * @return This text input for chaining
     */
    public TextInput setText(String text) {
        if (text == null) {
            text = "";
        }

        if (validator != null && !text.isEmpty() && !validator.test(text)) {
            return this;
        }

        if (text.length() > maxLength) {
            text = text.substring(0, maxLength);
        }

        if (!this.text.equals(text)) {
            this.text = text;
            setCursorToEnd();

            if (onChange != null) {
                onChange.accept(text);
            }
        }

        return this;
    }

    /**
     * Get the text
     * 
     * @return The text
     */
    public String getText() {
        return text;
    }

    /**
     * Set the placeholder text
     * 
     * @param placeholder The placeholder text
     * @return This text input for chaining
     */
    public TextInput setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    /**
     * Set the maximum length
     * 
     * @param maxLength The maximum length
     * @return This text input for chaining
     */
    public TextInput setMaxLength(int maxLength) {
        this.maxLength = maxLength;

        if (text.length() > maxLength) {
            setText(text.substring(0, maxLength));
        }

        return this;
    }

    /**
     * Set the change handler
     * 
     * @param onChange The change handler
     * @return This text input for chaining
     */
    public TextInput onChange(Consumer<String> onChange) {
        this.onChange = onChange;
        return this;
    }

    /**
     * Set the validator
     * 
     * @param validator The validator
     * @return This text input for chaining
     */
    public TextInput setValidator(Predicate<String> validator) {
        this.validator = validator;
        return this;
    }

    /**
     * Check if the text input is focused
     * 
     * @return True if the text input is focused
     */
    public boolean isFocused() {
        return focused;
    }

    /**
     * Set the focused state
     * 
     * @param focused The focused state
     * @return This text input for chaining
     */
    public TextInput setFocused(boolean focused) {
        this.focused = focused;
        return this;
    }

    @Override
    protected void renderContent(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY,
            float delta) {
        // Check if mouse is over using the absolute coordinates
        boolean hovered = isMouseOver(mouseX + absoluteX, mouseY + absoluteY);

        // Draw background
        int backgroundColor = focused ? 0xFF000000 : 0xFFFFFFFF;
        int borderColor = focused ? 0xFFFFFFFF : (hovered ? 0xFF000000 : 0xFFAAAAAA);
        int textColor = focused ? 0xFFFFFFFF : 0xFF000000;

        // Draw background and border
        context.fill(x, y, x + width, y + height, backgroundColor);
        context.drawBorder(x, y, width, height, borderColor);

        // Get text renderer
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int textY = y + (height - 8) / 2;

        // Calculate visible text
        String visibleText = getVisibleText(textRenderer, width - 10);

        // Draw text or placeholder
        if (text.isEmpty() && !placeholder.isEmpty()) {
            // Draw placeholder
            context.drawText(textRenderer, Text.literal(placeholder).formatted(Formatting.GRAY), x + 5, textY,
                    0xFF888888, false);
        } else {
            // Draw text
            context.drawText(textRenderer, visibleText, x + 5, textY, textColor, false);

            // Draw cursor
            if (focused) {
                focusedTicks++;

                if ((focusedTicks / 12) % 2 == 0) { // Slow down cursor blink rate
                    // Get cursor position
                    int cursorX = x + 5 + textRenderer.getWidth(visibleText.substring(0, getCursorVisiblePos()));

                    // Draw cursor
                    context.fill(cursorX, textY - 1, cursorX + 1, textY + 9, textColor);
                }
            } else {
                focusedTicks = 0;
            }
        }
    }

    /**
     * Get the visible text
     * 
     * @param textRenderer The text renderer
     * @param maxWidth     The maximum width
     * @return The visible text
     */
    private String getVisibleText(TextRenderer textRenderer, int maxWidth) {
        String visibleText = text;

        // Adjust display offset
        if (cursorPos < displayOffset) {
            displayOffset = cursorPos;
        }

        // Check if text is too long
        int totalWidth = textRenderer.getWidth(visibleText);
        if (totalWidth > maxWidth) {
            // Try to show as much text as possible
            while (displayOffset < text.length() && totalWidth > maxWidth) {
                visibleText = text.substring(displayOffset);
                totalWidth = textRenderer.getWidth(visibleText);

                if (totalWidth > maxWidth) {
                    displayOffset++;
                }
            }

            // If cursor is off screen to the right, adjust offset
            if (cursorPos > displayOffset) {
                String cursorToEnd = text.substring(displayOffset, cursorPos);
                int cursorToEndWidth = textRenderer.getWidth(cursorToEnd);

                if (cursorToEndWidth > maxWidth) {
                    // Move display offset to show cursor
                    displayOffset = Math.max(0, cursorPos - Math.max(1, maxWidth / textRenderer.getWidth("W")));

                    // Recalculate visible text
                    visibleText = text.substring(displayOffset);
                    totalWidth = textRenderer.getWidth(visibleText);

                    // Trim from the end if still too long
                    while (totalWidth > maxWidth && !visibleText.isEmpty()) {
                        visibleText = visibleText.substring(0, visibleText.length() - 1);
                        totalWidth = textRenderer.getWidth(visibleText);
                    }
                }
            }
        }

        return visibleText;
    }

    /**
     * Get the cursor position in the visible text
     * 
     * @return The cursor position in the visible text
     */
    private int getCursorVisiblePos() {
        return Math.max(0, cursorPos - displayOffset);
    }

    /**
     * Set the cursor to the end of the text
     */
    private void setCursorToEnd() {
        setCursorPos(text.length());
    }

    /**
     * Set the cursor position
     * 
     * @param pos The cursor position
     */
    private void setCursorPos(int pos) {
        cursorPos = MathHelper.clamp(pos, 0, text.length());
        if (!Screen.hasShiftDown()) {
            selectionEnd = cursorPos;
        }
    }

    /**
     * Get the character position at the given x coordinate
     * 
     * @param x The x coordinate
     * @return The character position
     */
    private int getCharPos(int x) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        String visibleText = text.substring(displayOffset);

        int charPos = 0;
        int currentX = 0;

        while (charPos < visibleText.length()) {
            int nextX = textRenderer.getWidth(visibleText.substring(0, charPos + 1));

            if (currentX + (nextX - currentX) / 2 > x) {
                break;
            }

            currentX = nextX;
            charPos++;
        }

        return displayOffset + charPos;
    }

    /**
     * Get the selected text
     * 
     * @return The selected text
     */
    private String getSelectedText() {
        int start = Math.min(cursorPos, selectionEnd);
        int end = Math.max(cursorPos, selectionEnd);

        return text.substring(start, end);
    }

    /**
     * Delete the selected text
     */
    private void deleteSelectedText() {
        if (cursorPos != selectionEnd) {
            int start = Math.min(cursorPos, selectionEnd);
            int end = Math.max(cursorPos, selectionEnd);

            String newText = text.substring(0, start) + text.substring(end);
            int oldCursorPos = cursorPos;
            setText(newText);
            setCursorPos(start);

            // Ensure cursor doesn't move unexpectedly
            if (oldCursorPos < selectionEnd) {
                selectionEnd = cursorPos;
            }
        }
    }

    /**
     * Write the given text at the cursor position
     * 
     * @param str The text to write
     */
    private void write(String str) {
        if (cursorPos != selectionEnd) {
            deleteSelectedText();
        }

        if (str.length() + text.length() > maxLength) {
            str = str.substring(0, maxLength - text.length());
        }

        if (!str.isEmpty()) {
            String newText = text.substring(0, cursorPos) + str + text.substring(cursorPos);

            if (validator == null || validator.test(newText)) {
                int oldCursorPos = cursorPos;
                setText(newText);
                setCursorPos(oldCursorPos + str.length());
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!enabled)
            return false;

        boolean wasClicked = isMouseOver(mouseX, mouseY);

        // Handle focus change
        if (wasClicked) {
            // This input was clicked
            boolean wasFocused = focused;
            focused = true;

            if (button == 0) {
                int relativeX = (int) mouseX - absoluteX - 5; // Adjust for the padding
                if (relativeX >= 0) {
                    setCursorPos(getCharPos(relativeX));
                } else {
                    setCursorPos(0);
                }
            }

            return true;
        } else {
            // Clicked elsewhere, lose focus
            if (focused) {
                focused = false;
            }
            return false;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return focused;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused)
            return false;

        switch (keyCode) {
            case 259: // Backspace
                if (cursorPos != selectionEnd) {
                    deleteSelectedText();
                } else if (cursorPos > 0) {
                    String newText = text.substring(0, cursorPos - 1) + text.substring(cursorPos);
                    int oldCursorPos = cursorPos;
                    setText(newText);
                    setCursorPos(oldCursorPos - 1); // Move cursor back one position
                }
                return true;

            case 261: // Delete
                if (cursorPos != selectionEnd) {
                    deleteSelectedText();
                } else if (cursorPos < text.length()) {
                    String newText = text.substring(0, cursorPos) + text.substring(cursorPos + 1);
                    int oldCursorPos = cursorPos;
                    setText(newText);
                    setCursorPos(oldCursorPos); // Keep cursor at same position
                }
                return true;

            case 262: // Right
                setCursorPos(cursorPos + 1);
                return true;

            case 263: // Left
                setCursorPos(cursorPos - 1);
                return true;

            case 264: // Down
            case 265: // Up
                return true;

            case 268: // Home
                setCursorPos(0);
                return true;

            case 269: // End
                setCursorPos(text.length());
                return true;

            case 341: // Ctrl+A (Select All)
            case 345:
                if (Screen.hasControlDown()) {
                    selectionEnd = 0;
                    setCursorPos(text.length());
                    return true;
                }
                break;

            case 67: // Ctrl+C (Copy)
                if (Screen.hasControlDown() && cursorPos != selectionEnd) {
                    MinecraftClient.getInstance().keyboard.setClipboard(getSelectedText());
                    return true;
                }
                break;

            case 86: // Ctrl+V (Paste)
                if (Screen.hasControlDown()) {
                    write(MinecraftClient.getInstance().keyboard.getClipboard());
                    return true;
                }
                break;

            case 88: // Ctrl+X (Cut)
                if (Screen.hasControlDown() && cursorPos != selectionEnd) {
                    MinecraftClient.getInstance().keyboard.setClipboard(getSelectedText());
                    deleteSelectedText();
                    return true;
                }
                break;
        }

        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!focused)
            return false;

        // Accept all printable characters
        if (chr >= 32 && chr != 127) {
            write(Character.toString(chr));
            return true;
        }

        return false;
    }
}