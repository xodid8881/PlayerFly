package org.hwabeag.playerfly.expansions;

import java.util.Objects;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.hwabeag.playerfly.PlayerFly;
import org.hwabeag.playerfly.config.ConfigManager;
import org.jetbrains.annotations.NotNull;

public class PlayerFlyExpansion extends PlaceholderExpansion {
    FileConfiguration PlayerFlyConfig = ConfigManager.getConfig("playerfly");
    private final PlayerFly plugin;

    public PlayerFlyExpansion(PlayerFly plugin) {
        this.plugin = plugin;
    }

    public boolean persist() {
        return true;
    }

    public boolean canRegister() {
        return true;
    }

    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getAuthors().toString();
    }

    public @NotNull String getIdentifier() {
        return "playerfly";
    }

    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("get")) { // %playerfly_get%
            String name = ((OfflinePlayer)Objects.requireNonNull(player)).getName();
            return this.PlayerFlyConfig.getString("플라이." + name + ".남은시간") == null ? null : this.PlayerFlyConfig.getString("플라이." + name + ".남은시간");
        } else {
            return null;
        }
    }
}