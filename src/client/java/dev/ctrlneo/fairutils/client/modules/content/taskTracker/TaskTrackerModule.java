package dev.ctrlneo.fairutils.client.modules.content.taskTracker;

import dev.ctrlneo.fairutils.client.modules.UtilityModule;

public class TaskTrackerModule extends UtilityModule {
    public static final String MODULE_CATEGORY = "Task Tracker";

    @Override
    public String getModuleName() {
        return MODULE_CATEGORY;
    }

    @Override
    public String getModuleId() {
        return "task_tracker";
    }
}
