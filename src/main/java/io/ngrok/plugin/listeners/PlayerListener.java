package io.ngrok.plugin.listeners;

import io.ngrok.plugin.managers.DatabaseManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        if (!DatabaseManager.getManager().playerExist(playerUUID)) {
            DatabaseManager.getManager().createPlayer(playerUUID);
        }
    }
}
