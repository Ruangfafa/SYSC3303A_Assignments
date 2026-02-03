package common;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;

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

    public Level getConsoleLevel() {
        return Level.parse(getString("CONSOLE_LEVEL"));
    }

    public Level getFileLevel() {
        return Level.parse(getString("FILE_LEVEL"));
    }

    public InetAddress getServerReceiveAddress(){
        try {
            return InetAddress.getByName(getString("SERVER_RECEIVE_ADDRESS"));
        } catch (UnknownHostException e) {
            return null;
        }

    }

    public int getServerReceivePort() {
        return getInt("SERVER_RECEIVE_PORT");
    }

    public InetAddress getIntermediateReceiveAddress(){
        try {
            return InetAddress.getByName(getString("INTERMEDIATE_RECEIVE_ADDRESS"));
        } catch (UnknownHostException e) {
            return null;
        }

    }

    public int getIntermediateReceivePort() {
        return getInt("INTERMEDIATE_RECEIVE_PORT");
    }
}