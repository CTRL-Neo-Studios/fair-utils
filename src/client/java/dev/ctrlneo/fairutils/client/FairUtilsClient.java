package dev.ctrlneo.fairutils.client;

import dev.ctrlneo.fairutils.client.config.FairUtilsConfig;
import dev.ctrlneo.fairutils.client.modules.content.damageIndicator.DamageIndicatorModule;
import dev.ctrlneo.fairutils.client.modules.content.listeners.ListenersModule;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.TaskTrackingModule;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.commands.TaskCommands;
import dev.ctrlneo.fairutils.client.modules.utility.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FairUtilsClient implements ClientModInitializer {

    public static final String CONFIG_FILE = "fairutils.config.json";

    public static final ModuleManager moduleManager = new ModuleManager();

    public final ListenersModule listenersModule = new ListenersModule();
    public final DamageIndicatorModule damageIndicatorModule = new DamageIndicatorModule();
    public final TaskTrackingModule taskTrackingModule = new TaskTrackingModule();

    private TaskCommands taskCommands;

    public static final Logger LOGGER = LoggerFactory.getLogger("Fair Utils");

    @Override
    public void onInitializeClient() {
        FairUtilsConfig.HANDLER.load();

        moduleManager.registerModule(listenersModule);
        moduleManager.registerModule(damageIndicatorModule);
        moduleManager.registerModule(taskTrackingModule);

        moduleManager.initializeModules();

        // Initialize task commands after modules are initialized
        taskCommands = new TaskCommands(taskTrackingModule.getStorage());
    }
}
