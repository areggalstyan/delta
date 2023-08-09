package com.aregcraft.delta.api.scheduler;

import com.aregcraft.delta.api.DeltaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SynchronousScheduler implements Scheduler {
    private final DeltaPlugin plugin;

    public SynchronousScheduler(DeltaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void scheduleTask(BukkitRunnable task) {
        task.runTask(plugin);
    }

    @Override
    public void scheduleDelayedTask(BukkitRunnable task, long delay) {
        task.runTaskLater(plugin, delay);
    }

    @Override
    public void scheduleRepeatingTask(BukkitRunnable task, long delay, long period) {
        task.runTaskTimer(plugin, delay, period);
    }
}
