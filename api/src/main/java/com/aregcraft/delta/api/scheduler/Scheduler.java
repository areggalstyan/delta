package com.aregcraft.delta.api.scheduler;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BiConsumer;

public interface Scheduler {
    void scheduleTask(BukkitRunnable task);

    void scheduleDelayedTask(BukkitRunnable task, long delay);

    void scheduleRepeatingTask(BukkitRunnable task, long delay, long period);

    default void scheduleTask(Runnable task) {
        scheduleTask(new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        });
    }

    default void scheduleDelayedTask(Runnable task, long delay) {
        scheduleDelayedTask(new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }, delay);
    }

    default void scheduleRepeatingTask(Runnable task, long delay, long period) {
        scheduleRepeatingTask(new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }, delay, period);
    }

    default void scheduleRepeatingTask(Runnable task, long period) {
        scheduleRepeatingTask(task, 0, period);
    }

    default void scheduleRepeatingTask(BiConsumer<BukkitRunnable, Integer> task, long delay, long period) {
        scheduleRepeatingTask(new BukkitRunnable() {
            private int i;

            @Override
            public void run() {
                task.accept(this, i++);
            }
        }, delay, period);
    }

    default void scheduleRepeatingTask(BiConsumer<BukkitRunnable, Integer> task, long period) {
        scheduleRepeatingTask(task, 0, period);
    }

    default void scheduleRepeatingTask(Runnable task, long delay, long period, long duration) {
        scheduleRepeatingTask((self, i) -> task.run(), delay, period, duration);
    }

    default void scheduleRepeatingTask(BiConsumer<BukkitRunnable, Integer> task, long delay, long period,
                                       long duration) {
        scheduleRepeatingTask((self, i) -> {
            if (i == duration) {
                self.cancel();
            }
            task.accept(self, i);
        }, delay, period);
    }
}
