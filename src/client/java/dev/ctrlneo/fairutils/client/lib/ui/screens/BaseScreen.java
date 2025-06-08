package dev.ctrlneo.fairutils.client.lib.ui.screens;

import dev.ctrlneo.fairutils.client.lib.ui.util.TemplateExpander;
import dev.ctrlneo.fairutils.client.utility.Reference;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import net.minecraft.util.Identifier;

public class BaseScreen extends BaseUIModelScreen<FlowLayout> {

    protected FlowLayout root;
    protected Identifier screenIdentifier;

    public BaseScreen(Identifier dataAssetIdentifier) {
        super(FlowLayout.class, DataSource.asset(dataAssetIdentifier));
        this.screenIdentifier = dataAssetIdentifier;
    }

    public BaseScreen() {
        this(Reference.of("default_screen"));
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        this.root = flowLayout;
        injectStaticComponents(flowLayout);
    }

    @Override
    protected void init() {
        super.init();
        if (this.uiAdapter == null) return;

        injectDynamicComponents();
    }

    /**
     * For Static Components. Runs in the `build()` function.
     * @param flowLayout
     */
    public void injectStaticComponents(FlowLayout flowLayout) {

    }

    /**
     * For Dynamic Components. Runs in the `init()` function.
     */
    public void injectDynamicComponents() {

    }

    public Identifier getScreenIdentifier() {
        return screenIdentifier;
    }

    public FlowLayout getRootComponent() {
        if (this.uiAdapter == null) return root;
        else root = this.uiAdapter.rootComponent;
        return root;
    }

    /**
     * Prepares a template for expansion.
     *
     * @param name The name of the template defined in your XML file.
     * @param type The expected class of the template's root component.
     * @param <T> The type of the template's root component.
     * @return A {@link TemplateExpander} builder to configure and expand the template.
     */
    protected <T extends Component> TemplateExpander<T> template(String name, Class<T> type) {
        return new TemplateExpander<>(this.model, name, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof BaseScreen) {
            return ((BaseScreen) obj).getScreenIdentifier().equals(this.getScreenIdentifier());
        }
        return false;
    }
}
