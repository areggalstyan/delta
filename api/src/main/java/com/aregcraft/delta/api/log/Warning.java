package com.aregcraft.delta.api.log;

import java.util.logging.Level;

public interface Warning extends Loggable {
    @Override
    default Level getLevel() {
        return Level.WARNING;
    }

    class Custom implements Warning {
        private final String message;

        public Custom(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    enum Default implements Warning {
        UPDATE_UNABLE_DELETE("Unable to delete the old version! Please do it manually.");

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
