package com.aregcraft.delta.api;

import com.aregcraft.delta.api.item.ItemWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;
import java.util.Map;

public class Recipe {
    private final List<String> shape;
    private final Map<Character, Material> ingredients;

    public Recipe(List<String> shape, Map<Character, Material> ingredients) {
        this.shape = shape;
        this.ingredients = ingredients;
    }

    public Material get(int index) {
        return ingredients.get(shape.get(index / 3).charAt(index % 3));
    }

    public void add(DeltaPlugin plugin, String key, ItemWrapper result) {
        var namespacedKey = new NamespacedKey(plugin, key);
        var recipe = new ShapedRecipe(namespacedKey, result.unwrap());
        recipe.shape(shape.toArray(String[]::new));
        ingredients.forEach(recipe::setIngredient);
        if (Bukkit.getRecipe(namespacedKey) != null) {
            Bukkit.removeRecipe(namespacedKey);
        }
        Bukkit.addRecipe(recipe);
        plugin.getRecipeDiscoverer().register(namespacedKey);
    }

    public void remove(DeltaPlugin plugin, String key) {
        var namespacedKey = new NamespacedKey(plugin, key);
        plugin.getRecipeDiscoverer().unregister(namespacedKey);
        Bukkit.removeRecipe(namespacedKey);
    }
}
