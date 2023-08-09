package com.aregcraft.delta.api;

import com.aregcraft.delta.api.block.custom.CustomBlockWrapper;
import com.aregcraft.delta.api.block.custom.RegisteredCustomBlock;
import com.aregcraft.delta.api.command.RegisteredCommand;
import com.aregcraft.delta.api.json.GsonFactory;
import com.aregcraft.delta.api.scheduler.AsynchronousScheduler;
import com.aregcraft.delta.api.scheduler.Scheduler;
import com.aregcraft.delta.api.scheduler.SynchronousScheduler;
import com.aregcraft.delta.api.util.Classes;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.ArrayList;
import java.util.List;

public abstract class DeltaPlugin extends JavaPlugin {
    private final Reflections reflections = new Reflections(DeltaPlugin.class, getClass());
    private final Reflections resourceReflections = new Reflections(Scanners.Resources);
    private final GsonFactory gsonFactory = new GsonFactory(this);
    private final Gson persistentDataGson = gsonFactory.create();
    private final Gson gson = gsonFactory.createWithPrettyPrinting();
    private final List<Runnable> enableHooks = new ArrayList<>();
    private final List<Runnable> disableHooks = new ArrayList<>();
    private final Scheduler synchronousScheduler = new SynchronousScheduler(this);
    private final Scheduler asynchronousScheduler = new AsynchronousScheduler(this);
    private final RecipeDiscoverer recipeDiscoverer = new RecipeDiscoverer(this);
    private Language language;

    @Override
    public void onEnable() {
        enableHooks.forEach(Runnable::run);
        registerListeners();
        registerCommands();
        registerCustomBlocks();
    }

    @Override
    public void onDisable() {
        disableHooks.forEach(Runnable::run);
    }

    public void registerEnableHook(Runnable hook) {
        if (isEnabled()) {
            hook.run();
            return;
        }
        enableHooks.add(hook);
    }

    public void registerDisableHook(Runnable hook) {
        disableHooks.add(hook);
    }

    protected void registerListeners() {
        reflections.getTypesAnnotatedWith(RegisteredListener.class).forEach(this::registerListener);
    }

    public void registerListener(Class<?> type) {
        registerListener(Classes.<Listener>newUncheckedInstanceWithPlugin(type, this));
    }

    public void registerListener(Listener listener) {
        if (isEnabled()) {
            Bukkit.getPluginManager().registerEvents(listener, this);
            return;
        }
        enableHooks.add(() -> registerListener(listener));
    }

    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    protected void registerCommands() {
        reflections.getTypesAnnotatedWith(RegisteredCommand.class).forEach(this::registerCommand);
    }

    private void registerCommand(Class<?> type) {
        var command = Classes.<TabExecutor>newUncheckedInstanceWithPlugin(type, this);
        if (command instanceof Listener listener) {
            registerListener(listener);
        }
        registerCommand(type.getAnnotation(RegisteredCommand.class).value(), command);
    }

    public void registerCommand(String name, TabExecutor tabExecutor) {
        var command = getCommand(name);
        if (command == null) {
            return;
        }
        command.setExecutor(tabExecutor);
        command.setTabCompleter(tabExecutor);
    }

    protected void registerCustomBlocks() {
        reflections.getTypesAnnotatedWith(RegisteredCustomBlock.class).forEach(this::registerCustomBlock);
    }

    private void registerCustomBlock(Class<?> type) {
        new CustomBlockWrapper(type.getAnnotation(RegisteredCustomBlock.class).value(), type, this);
    }

    public Reflections getReflections() {
        return reflections;
    }

    public Reflections getResourcesReflections() {
        return resourceReflections;
    }

    public Gson getPersistentDataGson() {
        return persistentDataGson;
    }

    public Gson getGson() {
        return gson;
    }

    public Scheduler getSynchronousScheduler() {
        return synchronousScheduler;
    }

    public Scheduler getAsynchronousScheduler() {
        return asynchronousScheduler;
    }

    public RecipeDiscoverer getRecipeDiscoverer() {
        return recipeDiscoverer;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
