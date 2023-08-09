package com.aregcraft.delta.api.log;

import java.util.logging.Level;

public interface Info extends Loggable {
    @Override
    default Level getLevel() {
        return Level.INFO;
    }

    class Custom implements Info {
        private final String message;

        public Custom(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    enum Default implements Info {
        UPDATE_ALREADY_LATEST("Already using the latest version of the plugin!"),
        UPDATE_SUCCESS("Successfully updated the plugin! Please restart the server."),
        CRASH("Use my discord server, providing the entire log if you need assistance.");

        private final String message;

        Default(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
