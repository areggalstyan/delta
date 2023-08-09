package com.aregcraft.delta.api;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.Set;

public class RecipeDiscoverer implements Listener {
    private final Set<NamespacedKey> recipes = new HashSet<>();

    public RecipeDiscoverer(DeltaPlugin plugin) {
        plugin.registerListener(this);
    }

    public void register(NamespacedKey key) {
        Bukkit.getOnlinePlayers().forEach(it -> it.discoverRecipe(key));
        recipes.add(key);
    }

    public void unregister(NamespacedKey key) {
        Bukkit.getOnlinePlayers().forEach(it -> it.undiscoverRecipe(key));
        recipes.remove(key);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().discoverRecipes(recipes);
    }
}
