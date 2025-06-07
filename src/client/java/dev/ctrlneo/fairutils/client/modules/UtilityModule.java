package dev.ctrlneo.fairutils.client.modules;

import dev.ctrlneo.fairutils.client.modules.event.ModuleToggledEvent;

public abstract class UtilityModule {

    protected boolean enabled = false;

    /**
     * Called once by the ModuleManager during client initialization.
     * Subclasses should override this to register keybindings,
     * persistent event listeners, and perform other setup.
     */
    public void initialize() {
        // Base implementation can be empty or provide common setup.
    }

    /**
     * @return A human-readable name for the module (e.g., "Boat Fly").
     */
    public abstract String getModuleName();

    /**
     * @return A unique machine-readable identifier for the module (e.g., "boat_fly").
     */
    public abstract String getModuleId();

    /**
     * Enables the module, if it's not already enabled.
     * Fires the ModuleToggledEvent [6] and calls onEnabled().
     */
    public void enableModule() {
        if (!enabled) {
            this.enabled = true;
            // Fire the generic event AFTER state has changed.
            ModuleToggledEvent.EVENT.invoker().onCallback(true, getModuleName(), getModuleId()); // [6]
            onEnabled(); // Call specific hook for subclasses.
        }
    }

    /**
     * Disables the module, if it's not already disabled.
     * Fires the ModuleToggledEvent [6] and calls onDisabled().
     */
    public void disableModule() {
        if (enabled) {
            this.enabled = false;
            // Fire the generic event AFTER state has changed.
            ModuleToggledEvent.EVENT.invoker().onCallback(false, getModuleName(), getModuleId()); // [6]
            onDisabled(); // Call specific hook for subclasses.
        }
    }

    /**
     * Toggles the current enabled state of the module.
     */
    public void toggleState() {
        if (this.enabled) {
            disableModule();
        } else {
            enableModule();
        }
    }

    /**
     * @return True if the module is currently enabled, false otherwise.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Hook method called when the module is enabled.
     * Subclasses can override this to perform actions specific to enabling.
     * Example: Registering dynamic event listeners, sending a chat message.
     */
    public void onEnabled() {
        // Example: System.out.println(getModuleName() + " enabled.");
    }

    /**
     * Hook method called when the module is disabled.
     * Subclasses can override this to perform actions specific to disabling.
     * Example: Unregistering dynamic event listeners.
     */
    public void onDisabled() {
        // Example: System.out.println(getModuleName() + " disabled.");
    }
}
