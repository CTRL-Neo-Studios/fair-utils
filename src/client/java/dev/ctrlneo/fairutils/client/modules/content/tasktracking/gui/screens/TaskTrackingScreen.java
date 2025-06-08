package dev.ctrlneo.fairutils.client.modules.content.tasktracking.gui.screens;

import dev.ctrlneo.fairutils.client.lib.ui.screens.BaseScreen;
import dev.ctrlneo.fairutils.client.utility.Reference;
import io.wispforest.owo.ui.container.FlowLayout;

import java.util.Map;

public class TaskTrackingScreen extends BaseScreen {

    protected TaskTrackingScreen() {
        super(Reference.of("tasktracking", "main_screen"));
    }

    @Override
    public void injectDynamicComponents() {
        super.injectDynamicComponents();
        getRootComponent().child(this.model.expandTemplate(FlowLayout.class, "", Map.of()).childById());
    }

    @Override
    public void injectStaticComponents(FlowLayout flowLayout) {
        super.injectStaticComponents(flowLayout);
    }
}
