package dev.ctrlneo.fairutils.client.lib.ui.util;

import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.parsing.UIModel;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple, fluent builder to simplify owo-ui template expansion.
 * This class handles resolving the full template reference and passing parameters.
 *
 * @param <T> The type of the template's root component.
 */
public class TemplateExpander<T extends Component> {

    private final UIModel model;
    private final String templateName;
    private final Class<T> type;
    private final Map<String, String> parameters = new HashMap<>();

    public TemplateExpander(UIModel model, String templateName, Class<T> type) {
        this.model = model;
        this.templateName = templateName;
        this.type = type;
    }

    /**
     * Adds a replacement parameter for a placeholder in the template.
     * For example, .with("custom-text", "Hello") for a {{custom-text}} placeholder.
     *
     * @return This builder instance for chaining.
     */
    public TemplateExpander<T> with(String key, String value) {
        this.parameters.put(key, value);
        return this;
    }

    /**
     * Adds multiple replacement parameters from a given map.
     *
     * @return This builder instance for chaining.
     */
    public TemplateExpander<T> with(Map<String, String> parameters) {
        this.parameters.putAll(parameters);
        return this;
    }

    /**
     * Expands the template using the configured parameters.
     * This resolves the full template reference automatically.
     *
     * @return The expanded component instance, ready to be configured or added to a parent.
     */
    public T expand(Identifier dataAssetIdentifier) {
        final String fullTemplateReference = TemplateHelper.resolveTemplateReference(templateName, dataAssetIdentifier);

        return this.model.expandTemplate(this.type, fullTemplateReference, this.parameters);
    }
}
