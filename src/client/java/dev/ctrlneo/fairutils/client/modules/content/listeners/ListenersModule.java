package dev.ctrlneo.fairutils.client.modules.content.listeners;

import dev.ctrlneo.fairutils.client.modules.UtilityModule;
import dev.ctrlneo.fairutils.client.modules.content.listeners.event.PlayerDamagedEvent;
import dev.ctrlneo.fairutils.client.modules.content.listeners.event.PlayerHealedEvent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ListenersModule extends UtilityModule {
    public double lastCheckedHealth = -1;

    @Override
    public void initialize() {
        super.initialize();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if(lastCheckedHealth == -1) lastCheckedHealth = client.player.getHealth();

            if (lastCheckedHealth > client.player.getHealth()) {
                PlayerDamagedEvent.EVENT.invoker().onCallback(client.player.getHealth(), lastCheckedHealth, client.player.getRecentDamageSource());
                lastCheckedHealth = client.player.getHealth();
            } else if (lastCheckedHealth < client.player.getHealth()) {
                PlayerHealedEvent.EVENT.invoker().onCallback(client.player.getHealth(), lastCheckedHealth);
                lastCheckedHealth = client.player.getHealth();
            }
        });
    }

    @Override
    public String getModuleName() {
        return "Listeners";
    }

    @Override
    public String getModuleId() {
        return "listeners";
    }
}
