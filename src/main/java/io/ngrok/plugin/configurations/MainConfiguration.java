package io.ngrok.plugin.configurations;

import io.ngrok.plugin.api.configuration.ConfigurationFile;
import org.bukkit.configuration.file.YamlConfiguration;

public class MainConfiguration extends ConfigurationFile {
    public MainConfiguration(String configName, String dir) {
        super(configName, dir);
        YamlConfiguration configuration = getConfiguration();
        if (isFirstTime()) {
            configuration.addDefault("configuration.mysql.host", "host");
            configuration.addDefault("configuration.mysql.port", "00");
            configuration.addDefault("configuration.mysql.database", "database");
            configuration.addDefault("configuration.mysql.username", "username");
            configuration.addDefault("configuration.mysql.password", "password");
            configuration.addDefault("configuration.mysql.config.connectionPoolSize", 20);
            configuration.addDefault("configuration.mysql.config.connectionPoolIdle", 20);
            configuration.addDefault("configuration.mysql.config.connectionPoolTimeout", 5000);
            configuration.addDefault("configuration.mysql.config.connectionPoolLifetime", 10800000);
        }
    }
}
