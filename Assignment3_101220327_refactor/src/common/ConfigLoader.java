package common;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;

public final class ConfigLoader {
    private static final ConfigLoader INSTANCE = new ConfigLoader();
    private final Path baseDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    private final Properties config = this.loadConfig();

    public static ConfigLoader getInstance() {
        return INSTANCE;
    }

    private ConfigLoader() {
    }

    private Properties loadConfig() {
        Path configPath = this.baseDir.resolve("config.properties");
        Properties props = new Properties();

        try {
            try (InputStream in = Files.newInputStream(configPath)) {
                props.load(in);
            }

            return props;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties at: " + String.valueOf(configPath), e);
        }
    }

    private Path getBaseDir() {
        return this.baseDir;
    }

    private String getString(String key) {
        return this.config.getProperty(key);
    }

    private String getString(String key, String defaultValue) {
        return this.config.getProperty(key, defaultValue);
    }

    private int getInt(String key) {
        return Integer.parseInt(this.config.getProperty(key));
    }

    private boolean getBoolean(String key) {
        return Boolean.parseBoolean(this.config.getProperty(key));
    }

    private Path getPath(String key) {
        return this.baseDir.resolve(this.config.getProperty(key));
    }

    public boolean ifLogOutput() {
        return this.getBoolean("LOG_OUTPUT");
    }

    public Level getConsoleLevel() {
        return Level.parse(this.getString("CONSOLE_LEVEL"));
    }

    public Level getFileLevel() {
        return Level.parse(this.getString("FILE_LEVEL"));
    }

    public int getGreenTimeOut() {
        return this.getInt("GREEN_TOUT");
    }

    public int getYellowTimeOut() {
        return this.getInt("YELLOW_TOUT");
    }

    public int getWalkTimeOut() {
        return this.getInt("WALK_TOUT");
    }

    public int getPedFlashN() {
        return this.getInt("PED_FLASH_N");
    }
}