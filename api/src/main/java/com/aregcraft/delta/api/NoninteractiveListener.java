package com.aregcraft.delta.api;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

@RegisteredListener
public class NoninteractiveListener implements Listener {
    public static final String NONINTERACTIVE_KEY = "noninteractive";

    @InjectPlugin
    private DeltaPlugin plugin;

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (isNoninteractive(event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    private boolean isNoninteractive(Entity entity) {
        return PersistentDataWrapper.wrap(plugin, entity).check(NONINTERACTIVE_KEY, true);
    }
}
