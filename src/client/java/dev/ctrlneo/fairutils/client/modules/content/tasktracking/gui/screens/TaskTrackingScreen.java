package dev.ctrlneo.fairutils.client.modules.content.tasktracking.gui.screens;

import dev.ctrlneo.fairutils.client.FairUtilsClient;
import dev.ctrlneo.fairutils.client.lib.ui.screens.BaseScreen;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.Task;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.TaskTrackingModule;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.utility.TaskStorage;
import dev.ctrlneo.fairutils.client.utility.Reference;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskTrackingScreen extends BaseScreen {

    public TaskTrackingScreen() {
        super(Reference.of("tasktracking", "main_screen"));
    }

    private List<FlowLayout> taskItems = new ArrayList<>();

    @Override
    public void injectDynamicComponents() {
        super.injectDynamicComponents();

        FlowLayout taskItemHolder = getRootComponent().childById(FlowLayout.class, "tasktracking.main_screen.task-item-holder");
        Optional<TaskStorage> storage = TaskTrackingModule.storage();
        if (taskItemHolder != null && storage.isPresent()) {
            taskItemHolder.clearChildren();
            taskItemHolder.<FlowLayout>configure(component -> {
                for(Task task : storage.get().getAllTasks()) {
                    // Replaces the label values of the task item
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


                    // Adds button functionality
                    ButtonComponent editButton = taskItem.childById(ButtonComponent.class, "tasktracking.task-item.edit"), deleteButton = taskItem.childById(ButtonComponent.class, "tasktracking.task-item.delete");

                    editButton.onPress(button -> {
                        // To Edit Task Screen
                    });
                    deleteButton.onPress(button -> {
                        storage.get().removeTask(task.getId());
                    });


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

    private void replaceTemplateLabels(FlowLayout taskItem) {

    }

    @Override
    public void injectStaticComponents(FlowLayout flowLayout) {
        super.injectStaticComponents(flowLayout);
    }
}
