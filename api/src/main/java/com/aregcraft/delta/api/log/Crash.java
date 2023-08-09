package com.aregcraft.delta.api.log;

import com.aregcraft.delta.api.DeltaPlugin;

public class Crash extends Error.Custom {
    private final Throwable throwable;

    public Crash(String message, Throwable throwable) {
        super(message);
        this.throwable = throwable;
    }

    @Override
    public void log(DeltaPlugin plugin, Object... args) {
        super.log(plugin, args);
        Info.Default.CRASH.log(plugin);
        throw new RuntimeException(throwable);
    }

    public enum Default implements CrashFactory {
        UPDATE_UNABLE_DOWNLOAD("An error occurred while trying to update the plugin!"),
        UPDATE_UNABLE_GET_LATEST("An error occurred while fetching the latest version!"),
        IO("An input/output error occurred!"),
        JSON_SYNTAX("An error occurred while trying to parse \"%s\"!");

        private final String message;

        Default(String message) {
            this.message = message;
        }

        @Override
        public Crash withThrowable(Throwable throwable) {
            return new Crash(message, throwable);
        }
    }

    private interface CrashFactory {
        Crash withThrowable(Throwable throwable);
    }
}
