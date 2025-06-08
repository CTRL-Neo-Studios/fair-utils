package dev.ctrlneo.fairutils.client.lib.ui;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles styling properties for UI components
 */
public class Style {
    private Map<String, Object> properties = new HashMap<>();

    // Padding
    public Style padding(int padding) {
        properties.put("padding", padding);
        return this;
    }

    public Style padding(int top, int right, int bottom, int left) {
        properties.put("paddingTop", top);
        properties.put("paddingRight", right);
        properties.put("paddingBottom", bottom);
        properties.put("paddingLeft", left);
        return this;
    }

    // Margin
    public Style margin(int margin) {
        properties.put("margin", margin);
        return this;
    }

    public Style margin(int top, int right, int bottom, int left) {
        properties.put("marginTop", top);
        properties.put("marginRight", right);
        properties.put("marginBottom", bottom);
        properties.put("marginLeft", left);
        return this;
    }

    // Width
    public Style width(int width) {
        properties.put("width", width);
        return this;
    }

    public Style widthFill() {
        properties.put("widthFill", true);
        return this;
    }

    public Style widthFitContent() {
        properties.put("widthFitContent", true);
        return this;
    }

    public Style widthPercent(float percent) {
        properties.put("widthPercent", percent);
        return this;
    }

    // Height
    public Style height(int height) {
        properties.put("height", height);
        return this;
    }

    public Style heightFill() {
        properties.put("heightFill", true);
        return this;
    }

    public Style heightFitContent() {
        properties.put("heightFitContent", true);
        return this;
    }

    public Style heightPercent(float percent) {
        properties.put("heightPercent", percent);
        return this;
    }

    // Flex properties
    public Style flexGrow(float grow) {
        properties.put("flexGrow", grow);
        return this;
    }

    public Style flexWrap() {
        properties.put("flexWrap", true);
        return this;
    }

    public Style flexColumn() {
        properties.put("flexColumn", true);
        return this;
    }

    public Style justifyContent(JustifyContent justify) {
        properties.put("justifyContent", justify);
        return this;
    }

    public Style alignItems(AlignItems align) {
        properties.put("alignItems", align);
        return this;
    }

    // Position
    public Style position(Position position) {
        properties.put("position", position);
        return this;
    }

    public Style top(int value) {
        properties.put("top", value);
        return this;
    }

    public Style right(int value) {
        properties.put("right", value);
        return this;
    }

    public Style bottom(int value) {
        properties.put("bottom", value);
        return this;
    }

    public Style left(int value) {
        properties.put("left", value);
        return this;
    }

    // Gap for flex containers
    public Style gap(int gap) {
        properties.put("gap", gap);
        return this;
    }

    // Animation and transition
    public Style transition(String property, int duration) {
        properties.put("transition", property);
        properties.put("transitionDuration", duration);
        return this;
    }

    // Getters
    public Object get(String property) {
        return properties.get(property);
    }

    public int getInt(String property, int defaultValue) {
        Object value = properties.get(property);
        return value instanceof Integer ? (int) value : defaultValue;
    }

    public float getFloat(String property, float defaultValue) {
        Object value = properties.get(property);
        return value instanceof Float ? (float) value : defaultValue;
    }

    public boolean getBoolean(String property, boolean defaultValue) {
        Object value = properties.get(property);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    // Enums for styling
    public enum JustifyContent {
        START, CENTER, END, SPACE_BETWEEN, SPACE_AROUND
    }

    public enum AlignItems {
        START, CENTER, END, STRETCH
    }

    public enum Position {
        RELATIVE, ABSOLUTE
    }
}