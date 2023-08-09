package com.aregcraft.delta.api.block;

import com.aregcraft.delta.api.DeltaPlugin;
import com.aregcraft.delta.api.PersistentDataWrapper;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BlockWrapper {
    private static final String KEY_TEMPLATE = "block_%d_%d_%d";

    private final DeltaPlugin plugin;
    private final PersistentDataContainer chunkDataContainer;
    private final NamespacedKey key;

    public static BlockWrapper wrap(Block block, DeltaPlugin plugin) {
        return new BlockWrapper(plugin, block);
    }

    public static BlockWrapper wrap(BlockState blockState, DeltaPlugin plugin) {
        return wrap(blockState.getBlock(), plugin);
    }

    public static BlockWrapper wrap(Location location, DeltaPlugin plugin) {
        return wrap(location.getBlock(), plugin);
    }

    private BlockWrapper(DeltaPlugin plugin, Block block) {
        this.plugin = plugin;
        chunkDataContainer = block.getChunk().getPersistentDataContainer();
        key = new NamespacedKey(plugin, KEY_TEMPLATE.formatted(block.getX() & 15, block.getY(), block.getZ() & 15));
    }

    public boolean hasPersistentData() {
        return chunkDataContainer.has(key, PersistentDataType.TAG_CONTAINER);
    }

    public PersistentDataWrapper getPersistentData() {
        return PersistentDataWrapper.wrap(plugin, getDataContainer(), this::setDataContainer);
    }

    public void setPersistentData(PersistentDataWrapper persistentData) {
        setDataContainer(persistentData.unwrap());
    }

    public void remove() {
        chunkDataContainer.remove(key);
    }

    private PersistentDataContainer getDataContainer() {
        return chunkDataContainer.getOrDefault(key, PersistentDataType.TAG_CONTAINER, newDataContainer());
    }

    private PersistentDataContainer newDataContainer() {
        return chunkDataContainer.getAdapterContext().newPersistentDataContainer();
    }

    private void setDataContainer(PersistentDataContainer persistentDataContainer) {
        chunkDataContainer.set(key, PersistentDataType.TAG_CONTAINER, persistentDataContainer);
    }
}
