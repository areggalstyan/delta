package com.aregcraft.delta.api.block;

import com.aregcraft.delta.api.DeltaPlugin;
import com.aregcraft.delta.api.InjectPlugin;
import com.aregcraft.delta.api.PersistentDataWrapper;
import com.aregcraft.delta.api.RegisteredListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RegisteredListener
public class BlockWrapperListener implements Listener {
    @InjectPlugin
    private DeltaPlugin plugin;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            remove(event.getBlock());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        var entity = event.getEntity();
        var block = event.getBlock();
        if (entity.getType() != EntityType.FALLING_BLOCK) {
            remove(block);
            return;
        }
        var persistentData = PersistentDataWrapper.wrap(plugin, entity);
        var to = event.getTo();
        if (to == Material.AIR && BlockWrapper.wrap(block, plugin).hasPersistentData()) {
            persistentData.set("from", block.getLocation());
            return;
        }
        if (to != Material.AIR && persistentData.has("from", Location.class)) {
            move(persistentData.get("from", Location.class).getBlock(), block);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().forEach(this::remove);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().forEach(this::remove);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBurn(BlockBurnEvent event) {
        remove(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockFade(BlockFadeEvent event) {
        remove(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockFertilize(BlockFertilizeEvent event) {
        event.getBlocks().stream().map(BlockState::getBlock).forEach(this::remove);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onStructureGrow(StructureGrowEvent event) {
        event.getBlocks().stream().map(BlockState::getBlock).forEach(this::remove);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        moveAll(event, new ArrayList<>(event.getBlocks()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        moveAll(event, new ArrayList<>(event.getBlocks()));
    }

    private void moveAll(BlockPistonEvent event, List<Block> blocks) {
        Collections.reverse(blocks);
        blocks.stream()
                .filter(it -> BlockWrapper.wrap(it, plugin).hasPersistentData())
                .forEach(it -> move(it, it.getRelative(event.getDirection())));
    }

    private void move(Block from, Block to) {
        BlockWrapper.wrap(to, plugin).setPersistentData(BlockWrapper.wrap(from, plugin).getPersistentData());
        remove(from);
    }

    private void remove(Block block) {
        BlockWrapper.wrap(block, plugin).remove();
    }
}
