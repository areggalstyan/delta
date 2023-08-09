package com.aregcraft.delta.api.scheduler;

import com.aregcraft.delta.api.DeltaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AsynchronousScheduler implements Scheduler {
    private final DeltaPlugin plugin;

    public AsynchronousScheduler(DeltaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void scheduleTask(BukkitRunnable task) {
        task.runTaskAsynchronously(plugin);
    }

    @Override
    public void scheduleDelayedTask(BukkitRunnable task, long delay) {
        task.runTaskLaterAsynchronously(plugin, delay);
    }

    @Override
    public void scheduleRepeatingTask(BukkitRunnable task, long delay, long period) {
        task.runTaskTimerAsynchronously(plugin, delay, period);
    }
}
