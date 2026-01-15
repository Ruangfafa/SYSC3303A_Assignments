package common;

import java.io.InputStream;
import java.nio.file.*;
import java.util.Properties;

public final class ConfigLoader {
    private static final ConfigLoader INSTANCE = new ConfigLoader();

    public static ConfigLoader getInstance() {
        return INSTANCE;
    }

    private final Path baseDir;
    private final Properties config;

    private ConfigLoader() {
        this.baseDir = Paths.get(System.getProperty("user.dir"))
                .toAbsolutePath();
        this.config = loadConfig();
    }

    private Properties loadConfig() {
        Path configPath = baseDir.resolve("config.ini");
        Properties props = new Properties();

        try (InputStream in = Files.newInputStream(configPath)) {
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load config.ini at: " + configPath, e
            );
        }
        return props;
    }

    public Path getBaseDir() {
        return baseDir;
    }

    public String getString(String key) {
        return config.getProperty(key);
    }

    public String getString(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }

    public int getInt(String key) {
        return Integer.parseInt(config.getProperty(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(config.getProperty(key));
    }

    public Path getPath(String key) {
        return baseDir.resolve(config.getProperty(key));
    }
}

/*
ConfigLoader config = ConfigLoader.getInstance();

String timeZone = config.getString("TIME_ZONE");
Path passwdFile = config.getPath("PASSWD_FILE");
 */