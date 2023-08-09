package com.aregcraft.delta.api.block.custom;

import com.aregcraft.delta.api.DeltaPlugin;
import com.aregcraft.delta.api.block.BlockWrapper;
import com.aregcraft.delta.api.item.ItemWrapper;
import com.aregcraft.delta.api.util.Classes;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.*;

public class CustomBlockWrapper implements Listener {
    private final Map<Location, CustomBlock> customBlocks = new HashMap<>();
    private final String id;
    private final Class<?> type;
    private final DeltaPlugin plugin;

    public CustomBlockWrapper(String id, Class<?> type, DeltaPlugin plugin) {
        this.id = id;
        this.type = type;
        this.plugin = plugin;
        plugin.registerListener(this);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!ItemWrapper.wrap(event.getItemInHand()).getPersistentData(plugin).check("id", id)) {
            return;
        }
        var block = event.getBlock();
        BlockWrapper.wrap(block, plugin).getPersistentData().set("id", id);
        getCustomBlock(block).onPlace(event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        if (event.isCancelled() || isNotThisBlock(block)) {
            return;
        }
        var customBlock = getCustomBlock(block);
        customBlock.onBreak(event);
        if (!event.isDropItems() || event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        event.setDropItems(false);
        customBlock.getItem().dropNaturally(block.getLocation());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        explodeCustomBlocks(event.blockList());
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        explodeCustomBlocks(event.blockList());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        var block = event.getClickedBlock();
        if (block == null|| event.getHand() != EquipmentSlot.HAND || isNotThisBlock(block)) {
            return;
        }
        var customBlock = getCustomBlock(block);
        customBlock.onClick(event);
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            customBlock.onLeftClick(event);
            return;
        }
        customBlock.onRightClick(event);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        var chunk = event.getChunk();
        chunk.getPersistentDataContainer().getKeys().stream()
                .map(NamespacedKey::getKey)
                .filter(it -> it.startsWith("block_"))
                .map(it -> getBlockAt(it, chunk))
                .forEach(this::loadCustomBlock);
    }

    private Block getBlockAt(String key, Chunk chunk) {
        var xyz = key.split("_");
        return chunk.getBlock(Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2]), Integer.parseInt(xyz[3]));
    }

    private void explodeCustomBlocks(List<Block> blocks) {
        blocks.removeIf(it -> {
            if (isNotThisBlock(it)) {
                return false;
            }
            it.setType(Material.AIR);
            getCustomBlock(it).getItem().dropNaturally(it.getLocation());
            return true;
        });
    }

    private CustomBlock getCustomBlock(Block block) {
        var location = block.getLocation();
        if (!customBlocks.containsKey(location)) {
            loadCustomBlock(block);
        }
        return customBlocks.get(location);
    }

    private void loadCustomBlock(Block block) {
        var customBlock = Classes.<CustomBlock>newUncheckedInstanceWithPlugin(type, plugin);
        customBlocks.put(block.getLocation(), customBlock);
        customBlock.onEnable(block);
        plugin.registerDisableHook(() -> customBlock.onDisable(block));
    }

    private boolean isNotThisBlock(Block block) {
        return !CustomBlock.check(block, id, plugin);
    }
}
