package dev.ctrlneo.fairutils.client.modules.utility;

import dev.ctrlneo.fairutils.client.modules.UtilityModule; // [1]
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModuleManager {
    private final List<UtilityModule> modules = new ArrayList<>();

    public void registerModule(UtilityModule module) {
        if (module != null && !modules.contains(module)) {
            modules.add(module);
            // You could log module registration here if desired
            // System.out.println("Registered module: " + module.getModuleName());
        }
    }

    /**
     * Calls the initialize() method on all registered modules.
     * This should be called once, typically in your ClientModInitializer.
     */
    public void initializeModules() {
        for (UtilityModule module : modules) {
            try {
                module.initialize(); // [1]
            } catch (Exception e) {
                // Log an error for the specific module that failed to initialize
                System.err.println("Failed to initialize module: " + module.getModuleName());
                e.printStackTrace();
            }
        }
    }

    public List<UtilityModule> getAllModules() {
        return new ArrayList<>(modules); // Return a copy to prevent external modification
    }

    public <T extends UtilityModule> Optional<T> getModule(Class<T> moduleClass) {
        for (UtilityModule module : modules) {
            if (moduleClass.isInstance(module)) {
                return Optional.of(moduleClass.cast(module));
            }
        }
        return Optional.empty();
    }

    public Optional<UtilityModule> getModuleById(String id) {
        for (UtilityModule module : modules) {
            if (module.getModuleId().equals(id)) { // [1]
                return Optional.of(module);
            }
        }
        return Optional.empty();
    }
}
