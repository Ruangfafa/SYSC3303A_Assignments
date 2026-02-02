package common;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        Path configPath = baseDir.resolve("config.properties");
        Properties props = new Properties();

        try (InputStream in = Files.newInputStream(configPath)) {
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load config.properties at: " + configPath, e
            );
        }
        return props;
    }

    private Path getBaseDir() {
        return baseDir;
    }

    private String getString(String key) {
        return config.getProperty(key);
    }

    private String getString(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }

    private int getInt(String key) {
        return Integer.parseInt(config.getProperty(key));
    }

    private boolean getBoolean(String key) {
        return Boolean.parseBoolean(config.getProperty(key));
    }

    private Path getPath(String key) {
        return baseDir.resolve(config.getProperty(key));
    }

    public boolean ifLogOutput() {
        return getBoolean("LOG_OUTPUT");
    }

    public int getServerReceivePort() {
        return getInt("SERVER_RECEIVE_PORT");
    }
}

/*
ConfigLoader config = ConfigLoader.getInstance();
 */