package io.ngrok.plugin.api.playerdata;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class PlayerData {
    UUID uuid;
    int coins;
    int kills;
    int deaths;
    int current_streak;
    int highest_streak;
    List<Integer> kits;

    public PlayerData(UUID uuid, int coins, int kills, int deaths, int current_streak, int highest_streak, List<Integer> kits) {
        this.uuid = uuid;
        this.coins = coins;
        this.kills = kills;
        this.deaths = deaths;
        this.current_streak = current_streak;
        this.highest_streak = highest_streak;
        this.kits = kits;
    }

    public void addCoins(int amount) {
        this.coins = this.coins + amount;
    }

    public void addKill() {
        this.kills++;
    }

    public void addDeath() {
        this.deaths++;
    }

    public void setCurrentStreak(int streak) {
        this.current_streak = streak;
    }

    public void setHighestStreak(int streak) {
        this.highest_streak = streak;
    }
}
