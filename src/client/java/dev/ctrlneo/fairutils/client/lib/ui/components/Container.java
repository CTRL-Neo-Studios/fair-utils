package dev.ctrlneo.fairutils.client.lib.ui.components;

import dev.ctrlneo.fairutils.client.lib.ui.AbstractComponent;
import dev.ctrlneo.fairutils.client.lib.ui.Component;
import dev.ctrlneo.fairutils.client.lib.ui.Style;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

/**
 * A container component that can hold multiple children with flex layout
 */
public class Container extends AbstractComponent {
    private final List<Component> children = new ArrayList<>();
    private int contentWidth;
    private int contentHeight;

    /**
     * Create a new container
     */
    public Container() {
        this.style.padding(0);
    }

    /**
     * Add a child component
     * 
     * @param child The child component
     * @return This container for chaining
     */
    public Container add(Component child) {
        children.add(child);
        return this;
    }

    /**
     * Remove a child component
     * 
     * @param child The child component
     * @return This container for chaining
     */
    public Container remove(Component child) {
        children.remove(child);
        return this;
    }

    /**
     * Clear all children
     * 
     * @return This container for chaining
     */
    public Container clear() {
        children.clear();
        return this;
    }

    @Override
    protected void renderContent(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY,
            float delta) {
        this.contentWidth = width;
        this.contentHeight = height;

        boolean isFlexColumn = style.getBoolean("flexColumn", false);
        boolean flexWrap = style.getBoolean("flexWrap", false);
        int gap = style.getInt("gap", 0);
        Style.JustifyContent justifyContent = (Style.JustifyContent) style.get("justifyContent");
        Style.AlignItems alignItems = (Style.AlignItems) style.get("alignItems");

        // Calculate total flex grow
        float totalFlexGrow = 0;
        for (Component child : children) {
            float flexGrow = child.getStyle().getFloat("flexGrow", 0);
            totalFlexGrow += flexGrow;
        }

        // Calculate remaining space after fixed size items
        int remainingSpace = isFlexColumn ? height : width;
        for (Component child : children) {
            if (child.getStyle().getFloat("flexGrow", 0) == 0) {
                remainingSpace -= isFlexColumn ? child.getHeight() : child.getWidth();
            }
        }
        // Subtract gaps
        if (children.size() > 1) {
            remainingSpace -= gap * (children.size() - 1);
        }

        // Position children
        int currentPos = 0;

        // Apply justify content (for main axis)
        if (justifyContent != null && totalFlexGrow == 0) {
            int totalSize = 0;
            for (Component child : children) {
                totalSize += isFlexColumn ? child.getHeight() : child.getWidth();
            }

            // Add gaps
            if (children.size() > 1) {
                totalSize += gap * (children.size() - 1);
            }

            int availableSpace = isFlexColumn ? height : width;
            int extraSpace = Math.max(0, availableSpace - totalSize);

            switch (justifyContent) {
                case CENTER:
                    currentPos = extraSpace / 2;
                    break;
                case END:
                    currentPos = extraSpace;
                    break;
                case SPACE_BETWEEN:
                    if (children.size() > 1) {
                        gap += extraSpace / (children.size() - 1);
                    }
                    break;
                case SPACE_AROUND:
                    if (children.size() > 0) {
                        int padding = extraSpace / (children.size() * 2);
                        currentPos = padding;
                        gap += padding * 2;
                    }
                    break;
                default: // START or null
                    break;
            }
        }

        int currentRow = 0;
        int rowHeight = 0;
        int rowStartPos = currentPos;

        for (int i = 0; i < children.size(); i++) {
            Component child = children.get(i);

            // Calculate size
            int childWidth = child.getWidth();
            int childHeight = child.getHeight();

            // Apply flex grow
            float flexGrow = child.getStyle().getFloat("flexGrow", 0);
            if (flexGrow > 0 && totalFlexGrow > 0) {
                if (isFlexColumn) {
                    childHeight = Math.round(remainingSpace * (flexGrow / totalFlexGrow));
                } else {
                    childWidth = Math.round(remainingSpace * (flexGrow / totalFlexGrow));
                }
            }

            // Check if we need to wrap
            if (flexWrap && !isFlexColumn && currentPos + childWidth > width && i > 0) {
                // Move to next row
                currentPos = 0;
                currentRow += rowHeight + gap;
                rowHeight = 0;
                rowStartPos = 0;
            }

            // Calculate cross axis position (based on align items)
            int crossPos = 0;
            if (alignItems != null) {
                int availableCrossSpace = isFlexColumn ? width : height;
                int childCrossSize = isFlexColumn ? childWidth : childHeight;

                switch (alignItems) {
                    case CENTER:
                        crossPos = (availableCrossSpace - childCrossSize) / 2;
                        break;
                    case END:
                        crossPos = availableCrossSpace - childCrossSize;
                        break;
                    case STRETCH:
                        if (isFlexColumn) {
                            childWidth = availableCrossSpace;
                        } else {
                            childHeight = availableCrossSpace;
                        }
                        break;
                    default: // START or null
                        break;
                }
            }

            // Render child
            int childX = isFlexColumn ? x + crossPos : x + currentPos;
            int childY = isFlexColumn ? y + currentPos : y + currentRow + crossPos;

            child.render(context, childX, childY, mouseX - childX, mouseY - childY, delta);

            // Update position
            if (isFlexColumn) {
                currentPos += childHeight + gap;
                rowHeight = Math.max(rowHeight, childWidth);
            } else {
                currentPos += childWidth + gap;
                rowHeight = Math.max(rowHeight, childHeight);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) {
            return false;
        }

        // Pass event to children in reverse order (top to bottom)
        for (int i = children.size() - 1; i >= 0; i--) {
            Component child = children.get(i);
            // Translate coordinates
            double childX = mouseX;
            double childY = mouseY;

            // Only pass event if within bounds
            if (childX >= 0 && childX < child.getWidth() && childY >= 0 && childY < child.getHeight()) {
                if (child.mouseClicked(childX, childY, button)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean handled = false;

        // Pass event to all children
        for (Component child : children) {
            if (child.mouseReleased(mouseX, mouseY, button)) {
                handled = true;
            }
        }

        return handled;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean handled = false;

        // Pass event to all children
        for (Component child : children) {
            if (child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                handled = true;
            }
        }

        return handled;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean handled = false;

        // Pass event to all children
        for (Component child : children) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) {
                handled = true;
            }
        }

        return handled;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        boolean handled = false;

        // Pass event to all children
        for (Component child : children) {
            if (child.keyReleased(keyCode, scanCode, modifiers)) {
                handled = true;
            }
        }

        return handled;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        boolean handled = false;

        // Pass event to all children
        for (Component child : children) {
            if (child.charTyped(chr, modifiers)) {
                handled = true;
            }
        }

        return handled;
    }
}