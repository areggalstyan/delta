package com.aregcraft.delta.api.block.custom;

import com.aregcraft.delta.api.DeltaPlugin;
import com.aregcraft.delta.api.block.BlockWrapper;
import com.aregcraft.delta.api.item.ItemWrapper;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public interface CustomBlock {
    static boolean check(Block block, String id, DeltaPlugin plugin) {
        return block != null && BlockWrapper.wrap(block, plugin).getPersistentData().check("id", id);
    }

    ItemWrapper getItem();

    default void onEnable(Block block) {
    }

    default void onDisable(Block block) {
    }

    default void onPlace(BlockPlaceEvent event) {
    }

    default void onBreak(BlockBreakEvent event) {
    }

    default void onClick(PlayerInteractEvent event) {
    }

    default void onRightClick(PlayerInteractEvent event) {
    }

    default void onLeftClick(PlayerInteractEvent event) {
    }
}
