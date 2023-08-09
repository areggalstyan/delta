package com.aregcraft.delta.api.log;

import java.util.logging.Level;

public interface Error extends Loggable {
    @Override
    default Level getLevel() {
        return Level.SEVERE;
    }

    class Custom implements Error {
        private final String message;

        public Custom(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    enum Default implements Error {
        LOAD_DUPLICATE_ID("Duplicate id \"%s\" found in \"%s\"!");

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
