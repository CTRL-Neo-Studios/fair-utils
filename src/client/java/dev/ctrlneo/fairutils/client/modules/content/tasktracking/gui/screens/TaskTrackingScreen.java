package dev.ctrlneo.fairutils.client.modules.content.tasktracking.gui.screens;

import dev.ctrlneo.fairutils.client.lib.ui.screens.BaseScreen;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.Task;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.TaskTrackingModule;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.utility.TaskManager;
import dev.ctrlneo.fairutils.client.utility.Reference;
import io.wispforest.owo.ui.container.FlowLayout;

import java.util.Map;

public class TaskTrackingScreen extends BaseScreen {

    public TaskTrackingScreen() {
        super(Reference.of("tasktracking", "main_screen"));
    }

    @Override
    public void injectDynamicComponents() {
        super.injectDynamicComponents();

        FlowLayout taskItemHolder = getRootComponent().childById(FlowLayout.class, "task-item-holder");
        if (taskItemHolder != null) {
            taskItemHolder.clearChildren();
            taskItemHolder.<FlowLayout>configure(component -> {
                for(Task task : TaskTrackingModule.TASK_MANAGER.getAllTasks()) {
                    component.child(
                            template("task-item", FlowLayout.class)
                                    .with("task-title", task.getTitle())
                                    .with("task-desc", task.getDescription())
                                    .with("percentage", String.valueOf((int)(task.getProgress() * 100)) + "%")
                                    .expand(Reference.of("tasktracking", "components", "task_item"))
                    );
                }
            });
        }
    }

    @Override
    public void injectStaticComponents(FlowLayout flowLayout) {
        super.injectStaticComponents(flowLayout);
    }
}
