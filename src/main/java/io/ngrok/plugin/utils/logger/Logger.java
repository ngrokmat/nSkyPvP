package io.ngrok.plugin.utils.logger;

import io.ngrok.plugin.managers.DatabaseManager;
import io.ngrok.plugin.utils.Utils;
import io.ngrok.plugin.utils.logger.types.Types;
import org.bukkit.Bukkit;

public class Logger {

    public static void log(Types type, String message) {
        DatabaseManager.getManager().
        switch (type) {
            case INFO:
                Bukkit.getConsoleSender().sendMessage(Utils.colorize("&7[SkyPvP LOGGER / INFO] " + message));
                break;
            case WARN:
                Bukkit.getConsoleSender().sendMessage(Utils.colorize("&6[SkyPvP LOGGER / WARN] " + message));
                break;
            case ERROR:
                Bukkit.getConsoleSender().sendMessage(Utils.colorize("&c[SkyPvP LOGGER / ERROR] " + message));
        }
    }
}
