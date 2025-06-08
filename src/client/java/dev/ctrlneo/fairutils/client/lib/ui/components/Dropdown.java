package dev.ctrlneo.fairutils.client.lib.ui.components;

import dev.ctrlneo.fairutils.client.lib.ui.AbstractComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A dropdown component for selecting from a list of options
 */
public class Dropdown<T> extends AbstractComponent {
    private final List<Option<T>> options = new ArrayList<>();
    private Option<T> selectedOption;
    private boolean expanded = false;
    private Consumer<T> onSelect;
    private int maxDisplayedOptions = 5;
    private int hoveredIndex = -1;

    /**
     * Create a new dropdown
     */
    public Dropdown() {
        this.width = 150;
        this.height = 20;
    }

    /**
     * Add an option to the dropdown
     * 
     * @param option The option to add
     * @return This dropdown for chaining
     */
    public Dropdown<T> addOption(Option<T> option) {
        options.add(option);

        // If this is the first option, select it
        if (selectedOption == null) {
            selectedOption = option;
        }

        return this;
    }

    /**
     * Add an option to the dropdown
     * 
     * @param label The option label
     * @param value The option value
     * @return This dropdown for chaining
     */
    public Dropdown<T> addOption(String label, T value) {
        return addOption(new Option<>(label, value));
    }

    /**
     * Set the selected option
     * 
     * @param value The value to select
     * @return This dropdown for chaining
     */
    public Dropdown<T> setSelected(T value) {
        for (Option<T> option : options) {
            if (option.value.equals(value)) {
                selectedOption = option;
                break;
            }
        }
        return this;
    }

    /**
     * Set the selected option by index
     * 
     * @param index The index to select
     * @return This dropdown for chaining
     */
    public Dropdown<T> setSelectedIndex(int index) {
        if (index >= 0 && index < options.size()) {
            selectedOption = options.get(index);
        }
        return this;
    }

    /**
     * Get the selected value
     * 
     * @return The selected value
     */
    public T getSelected() {
        return selectedOption != null ? selectedOption.value : null;
    }

    /**
     * Set the selection handler
     * 
     * @param onSelect The selection handler
     * @return This dropdown for chaining
     */
    public Dropdown<T> onSelect(Consumer<T> onSelect) {
        this.onSelect = onSelect;
        return this;
    }

    /**
     * Set the maximum number of options to display when expanded
     * 
     * @param maxDisplayedOptions The maximum number of options to display
     * @return This dropdown for chaining
     */
    public Dropdown<T> setMaxDisplayedOptions(int maxDisplayedOptions) {
        this.maxDisplayedOptions = maxDisplayedOptions;
        return this;
    }

    @Override
    protected void renderContent(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY,
            float delta) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        boolean hovered = mouseX >= 0 && mouseY >= 0 && mouseX < width && mouseY < height;

        // Draw main dropdown box
        int backgroundColor = expanded ? 0xFF000000 : (hovered ? 0xFF000000 : 0xFFFFFFFF);
        int textColor = expanded ? 0xFFFFFFFF : (hovered ? 0xFFFFFFFF : 0xFF000000);
        int borderColor = enabled ? 0xFFFFFFFF : 0xFF555555;

        // Draw background
        context.fill(x, y, x + width, y + height, backgroundColor);

        // Draw border
        context.drawBorder(x, y, width, height, borderColor);

        // Draw selected text
        String displayText = selectedOption != null ? selectedOption.label : "";
        context.drawText(textRenderer, displayText, x + 5, y + (height - 8) / 2, textColor, false);

        // Draw dropdown arrow
        int arrowX = x + width - 15;
        int arrowY = y + height / 2;
        int arrowSize = 5;

        for (int i = 0; i < arrowSize; i++) {
            context.fill(arrowX + i, expanded ? arrowY + i : arrowY - i,
                    arrowX + 2 * arrowSize - i, expanded ? arrowY + i + 1 : arrowY - i + 1,
                    textColor);
        }

        // Draw dropdown options if expanded
        if (expanded && !options.isEmpty()) {
            int optionHeight = height;
            int visibleOptions = Math.min(options.size(), maxDisplayedOptions);
            int totalHeight = visibleOptions * optionHeight;

            // Draw dropdown background
            context.fill(x, y + height, x + width, y + height + totalHeight, 0xFF000000);
            context.drawBorder(x, y + height, width, totalHeight, 0xFFFFFFFF);

            // Draw options
            hoveredIndex = -1;

            for (int i = 0; i < visibleOptions; i++) {
                Option<T> option = options.get(i);
                int optionY = y + height + i * optionHeight;

                boolean optionHovered = mouseX >= 0 && mouseX < width &&
                        mouseY >= optionY - y && mouseY < optionY - y + optionHeight;

                if (optionHovered) {
                    hoveredIndex = i;
                    context.fill(x + 1, optionY, x + width - 1, optionY + optionHeight, 0xFF333333);
                }

                context.drawText(textRenderer, option.label, x + 5, optionY + (optionHeight - 8) / 2, 0xFFFFFFFF,
                        false);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) {
            // Click outside the dropdown should close it
            if (expanded) {
                expanded = false;
                return true;
            }
            return false;
        }

        // If clicking on the main dropdown area, toggle expanded state
        if (mouseY < height) {
            expanded = !expanded;
            return true;
        }

        // If clicking on an option
        if (expanded && button == 0 && hoveredIndex >= 0 && hoveredIndex < options.size()) {
            Option<T> option = options.get(hoveredIndex);
            selectedOption = option;
            expanded = false;

            if (onSelect != null) {
                onSelect.accept(option.value);
            }

            return true;
        }

        return false;
    }

    /**
     * A dropdown option
     */
    public static class Option<T> {
        private final String label;
        private final T value;

        /**
         * Create a new option
         * 
         * @param label The option label
         * @param value The option value
         */
        public Option(String label, T value) {
            this.label = label;
            this.value = value;
        }

        /**
         * Get the option label
         * 
         * @return The option label
         */
        public String getLabel() {
            return label;
        }

        /**
         * Get the option value
         * 
         * @return The option value
         */
        public T getValue() {
            return value;
        }
    }
}