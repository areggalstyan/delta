package com.aregcraft.delta.api.log;

import com.aregcraft.delta.api.DeltaPlugin;
import com.aregcraft.delta.api.FormattingContext;

import java.util.logging.Level;

public interface Loggable {
    String getMessage();

    Level getLevel();

    default void log(DeltaPlugin plugin, Object... args) {
        plugin.getLogger().log(getLevel(), FormattingContext.withPlugin(plugin).format(getMessage().formatted(args)));
    }
}
