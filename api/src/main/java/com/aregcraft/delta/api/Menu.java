package com.aregcraft.delta.api;

import com.aregcraft.delta.api.item.ItemWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class Menu {
    private final String title;
    private final int size;

    public Menu(String title, int size) {
        this.title = title;
        this.size = size;
    }

    public void open(Player player, List<ItemWrapper> items) {
        var inventory = Bukkit.createInventory(player, size, title);
        items.stream().map(ItemWrapper::unwrap).forEach(inventory::addItem);
        player.openInventory(inventory);
    }
}
