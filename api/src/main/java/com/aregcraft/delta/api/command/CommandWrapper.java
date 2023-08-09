package com.aregcraft.delta.api.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public interface CommandWrapper extends TabExecutor {
    default boolean execute(Player sender, List<String> args) {
        return false;
    }

    default boolean execute(CommandSender sender, List<String> args) {
        if (sender instanceof Player player) {
            return execute(player, args);
        }
        return false;
    }

    default List<String> suggest(Player sender, List<String> args) {
        return null;
    }

    default List<String> suggest(CommandSender sender, List<String> args) {
        if (sender instanceof Player player) {
            return suggest(player, args);
        }
        return null;
    }

    @Override
    default boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execute(sender, List.of(args));
    }

    @Override
    default List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return suggest(sender, List.of(args));
    }
}
