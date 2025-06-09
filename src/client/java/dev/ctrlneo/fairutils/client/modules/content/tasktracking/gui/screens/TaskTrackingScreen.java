package dev.ctrlneo.fairutils.client.modules.content.tasktracking.gui.screens;

import dev.ctrlneo.fairutils.client.FairUtilsClient;
import dev.ctrlneo.fairutils.client.lib.ui.screens.BaseScreen;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.Task;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.TaskTrackingModule;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.utility.TaskManager;
import dev.ctrlneo.fairutils.client.utility.Reference;
import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskTrackingScreen extends BaseScreen {

    public TaskTrackingScreen() {
        super(Reference.of("tasktracking", "main_screen"));
    }

    private List<FlowLayout> taskItems = new ArrayList<>();

    @Override
    public void injectDynamicComponents() {
        super.injectDynamicComponents();

        FlowLayout taskItemHolder = getRootComponent().childById(FlowLayout.class, "tasktracking.main_screen.task-item-holder");
        if (taskItemHolder != null) {
            taskItemHolder.clearChildren();
            taskItemHolder.<FlowLayout>configure(component -> {
                for(Task task : TaskTrackingModule.TASK_MANAGER.getAllTasks()) {
                    FlowLayout taskItem = template("tasktracking.task-item", FlowLayout.class)
                            .with("task-title", task.getTitle())
                            .with("task-desc", task.getDescription())
                            .with("percentage", String.valueOf((int)(task.getProgress() * 100)) + "%")
                            .expand(Reference.of("tasktracking", "components", "task_item"));

                    taskItem.childById(FlowLayout.class, "tasktracking.task-item.task-progress").horizontalSizing(Sizing.fill((int)(task.getProgress() * 100)));
                    FlowLayout taskProgress = taskItem.childById(FlowLayout.class, "tasktracking.task-item.task-progress");
                    if (taskProgress != null) {
                        taskProgress.horizontalSizing(Sizing.fill((int)(task.getProgress() * 100)));
                    }

                    // TODO: Add Edit and Delete Button Implementation Here.

                    FairUtilsClient.LOGGER.info(String.format("progress %s; objectives %s; completed %s; ongoing %s;", String.valueOf(
                            task.getProgress()),
                            task.getObjectives().stream().count(),
                            task.getObjectives().stream().filter(taskObjective -> taskObjective.isCompleted()).count(),
                            task.getObjectives().stream().filter(taskObjective -> !taskObjective.isCompleted()).count()
                    ));

                    component.child(taskItem);
                }
            });
        }
    }

    @Override
    public void injectStaticComponents(FlowLayout flowLayout) {
        super.injectStaticComponents(flowLayout);
    }
}
