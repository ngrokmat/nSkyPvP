package io.ngrok.plugin.managers;

import io.ngrok.plugin.api.playerdata.PlayerData;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerDataManager {
    @Getter
    private static PlayerDataManager manager = new PlayerDataManager();
    @Getter
    private HashMap<UUID, PlayerData> players = new HashMap<>();

    public void loadPlayerData(UUID uuid) {
        PlayerData playerData = DatabaseManager.getManager().getPlayerData(uuid);
        players.put(uuid, playerData);
    }

    public void unloadPlayerData(UUID uuid) {
        players.remove(uuid);
    }

    public void createPlayerData(UUID uuid) {
        DatabaseManager.getManager().createPlayer(uuid);
    }

    public PlayerData getPlayerData(UUID uuid) {
        return players.get(uuid);
    }

    public void addCoins(UUID uuid, int amount) {
        getPlayerData(uuid).addCoins(amount);
    }

    public void addKill(UUID uuid) {
        getPlayerData(uuid).addKill();
    }

    public void addDeath(UUID uuid) {
        getPlayerData(uuid).addDeath();
    }

    public void setCurrentStreak(UUID uuid, int streak) {
        getPlayerData(uuid).setCurrentStreak(streak);
    }

    public void setHighestStreak(UUID uuid, int streak) {
        getPlayerData(uuid).setHighestStreak(streak);
    }
}
