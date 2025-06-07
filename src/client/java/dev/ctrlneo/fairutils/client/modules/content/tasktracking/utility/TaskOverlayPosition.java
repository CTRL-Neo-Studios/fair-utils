package dev.ctrlneo.fairutils.client.modules.content.tasktracking.utility;

/**
 * Enum for task overlay position on screen
 */
public enum TaskOverlayPosition {
    TOP_LEFT("Top Left"),
    TOP_RIGHT("Top Right"),
    BOTTOM_LEFT("Bottom Left"),
    BOTTOM_RIGHT("Bottom Right");

    private final String displayName;

    TaskOverlayPosition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}