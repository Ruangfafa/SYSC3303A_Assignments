package common;

/**
 * Centralized constants for the whole application.
 * All fixed strings, log messages, and config keys live here.
 */
public final class Constants {
    public static final class Application {
        public static final String
                LOGGER = "application",
                MAIN_MODULE = "__main__";
        private Application() {}
    }
    private Constants() {
    }
}
